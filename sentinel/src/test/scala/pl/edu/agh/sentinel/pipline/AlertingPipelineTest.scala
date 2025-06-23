package pl.edu.agh.sentinel.pipline

import zio._
import zio.kafka.serde.*
import zio.stream.ZStream
import zio.test.*

import java.time.Instant

import pl.edu.agh.sentinel.SentinelApp.runAlertingPipeline
import pl.edu.agh.sentinel.configs.{ DiscordConfig, EmailConfig, NotificationConfig, SlackConfig }
import pl.edu.agh.sentinel.events.{ AlertEvent, TaskEvent }
import pl.edu.agh.sentinel.kafka.*
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.{ ConsumingStrategy, KafkaConsumer, TaskEventConsumer }
import pl.edu.agh.sentinel.kafka.consumers.ConsumingStrategy.Earliest
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.{ KafkaTopic, TopicManager }
import pl.edu.agh.sentinel.notifications.{ NotificationEnv, Notifier, SentinelNotifier }
import pl.edu.agh.sentinel.processing.AlertingEngine

object AlertingPipelineFunctionTest extends ZIOSpecDefault {
  given Serializer[Any, String] = Serde.string
  given Deserializer[Any, String] = Serde.string
  given Serializer[Any, TaskEvent] = ZioJsonSerde[TaskEvent]
  given Deserializer[Any, TaskEvent] = ZioJsonSerde[TaskEvent]

  val testEnvLayer: ZLayer[Scope, Throwable, KafkaTestEnv] =
    KafkaTestEnv.layer(Earliest)

  val kafkaConfigLayerFromEnv: ZLayer[KafkaTestEnv, Nothing, KafkaConfig] =
    ZLayer.fromZIO(ZIO.serviceWith[KafkaTestEnv](_.config))

  val topicManagerLayer: ZLayer[KafkaTestEnv & Scope, Throwable, TopicManager] =
    (kafkaConfigLayerFromEnv ++ ZLayer.fromZIO(ZIO.scope)) >>> TopicManager.layer

  val alertingEngineLayer: ZLayer[Any, Nothing, AlertingEngine] =
    ZLayer.succeed(TestEngine())

  val notificationConfigLayer: ZLayer[Any, Nothing, NotificationConfig] = {
    ZLayer.succeed(
      NotificationConfig(
        enabled = false,
        slack = SlackConfig(enabled = false, token = None),
        discord = DiscordConfig(enabled = false, webhookUrl = None),
        email = EmailConfig(enabled = false, smtpServer = None, smtpPort = None, fromAddress = None),
      )
    )
  }

  val sentinelNotifierLayer: ZLayer[NotificationConfig, Throwable, SentinelNotifier] =
    SentinelNotifier.live

  val testLayers
    : ZLayer[Scope, Throwable, KafkaTestEnv & TopicManager & KafkaConsumer & KafkaConfig & KafkaProducer & AlertingEngine & NotificationConfig & SentinelNotifier] = {
    testEnvLayer >+> kafkaConfigLayerFromEnv >+> topicManagerLayer >+>
      ZLayer.fromZIO(ZIO.service[KafkaTestEnv].flatMap(_.getConsumer(Earliest))) >+>
      ZLayer.fromZIO(ZIO.service[KafkaTestEnv].flatMap(_.getProducer)) >+>
      alertingEngineLayer >+>
      notificationConfigLayer >+>
      sentinelNotifierLayer
  }

  private val topic = KafkaTopic("task-events")

  def makeTestNotifier(ref: Ref[List[AlertEvent]]): Notifier = new Notifier {
    override def send(message: AlertEvent): Task[Unit] =
      ref.update(_ :+ message)
  }

  def inMemoryTest: ZIO[Any, Nothing, TestResult] = for {
    queue <- Queue.unbounded[TaskEvent]
    alertRef <- Ref.make(List.empty[AlertEvent])
    notifier = makeTestNotifier(alertRef)
    engine = TestEngine()
    taskEventStream = ZStream.fromQueue(queue)
    fiber <- engine
      .process(taskEventStream)
      .tap(alert => notifier.send(alert))
      .runDrain
      .fork
    event = TaskEvent.TaskCreated("task-123", "Important", "team-1", "col-1", "user-1", Instant.now().nn)
    _ <- queue.offer(event)
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.exists(_.message.contains("task-123")))

  def runTestAlertingPipeline(
    taskEventStream: ZStream[Any, Throwable, TaskEvent],
    producer: KafkaProducer,
    engine: AlertingEngine,
  ): ZIO[KafkaEnv & AlertingEngine & NotificationEnv, Throwable, Fiber.Runtime[Throwable, Unit]] = {
    runAlertingPipeline(taskEventStream, producer, engine).fork
  }

  // Provide both NotificationConfig and SentinelNotifier
  def testSentinelNotifierLayer(alertRef: Ref[List[AlertEvent]], testNotifier: Notifier)
    : ZLayer[Any, Nothing, NotificationConfig & SentinelNotifier] =
    notificationConfigLayer ++ ZLayer.succeed(new SentinelNotifier(List(testNotifier)))

  val test = for {
    queue <- Queue.unbounded[TaskEvent]
    alertRef <- Ref.make(List.empty[AlertEvent])
    testNotifier = new Notifier {
      override def send(message: AlertEvent): Task[Unit] = alertRef.update(_ :+ message)
    }
    engine = TestEngine()
    producer <- ZIO.service[KafkaProducer]
    taskEventStream = ZStream.fromQueue(queue)
    fiber <- runTestAlertingPipeline(taskEventStream, producer, engine)
      .provideSomeLayer[KafkaEnv & AlertingEngine](testSentinelNotifierLayer(alertRef, testNotifier))
    event = TaskEvent.TaskCreated("task-123", "Important", "team-1", "col-1", "user-1", Instant.now().nn)
    _ <- queue.offer(event)
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.exists(_.message.contains("task-123")))

  val noAlertOnEmptyInput: ZIO[Any, Nothing, TestResult] = for {
    queue <- Queue.unbounded[TaskEvent]
    alertRef <- Ref.make(List.empty[AlertEvent])
    notifier = makeTestNotifier(alertRef)
    engine = TestEngine()
    fiber <- engine.process(ZStream.fromQueue(queue)).tap(alert => notifier.send(alert)).runDrain.fork
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.isEmpty)

  val multipleAlerts: ZIO[Any, Nothing, TestResult] = for {
    queue <- Queue.unbounded[TaskEvent]
    alertRef <- Ref.make(List.empty[AlertEvent])
    notifier = makeTestNotifier(alertRef)
    engine = TestEngine()
    fiber <- engine.process(ZStream.fromQueue(queue)).tap(alert => notifier.send(alert)).runDrain.fork
    _ <- queue.offer(TaskEvent.TaskCreated("task-1", "A", "team", "col", "user", Instant.now().nn))
    _ <- queue.offer(TaskEvent.TaskCreated("task-2", "B", "team", "col", "user", Instant.now().nn))
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.size == 2)

  val duplicateEvents: ZIO[Any, Nothing, TestResult] = for {
    queue <- Queue.unbounded[TaskEvent]
    alertRef <- Ref.make(List.empty[AlertEvent])
    notifier = makeTestNotifier(alertRef)
    engine = TestEngine()
    fiber <- engine.process(ZStream.fromQueue(queue)).tap(alert => notifier.send(alert)).runDrain.fork
    event = TaskEvent.TaskCreated("task-1", "A", "team", "col", "user", Instant.now().nn)
    _ <- queue.offer(event)
    _ <- queue.offer(event)
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.size == 2)

  val alertContentTest: ZIO[Any, Nothing, TestResult] = for {
    queue <- Queue.unbounded[TaskEvent]
    alertRef <- Ref.make(List.empty[AlertEvent])
    notifier = makeTestNotifier(alertRef)
    engine = TestEngine()
    fiber <- engine.process(ZStream.fromQueue(queue)).tap(alert => notifier.send(alert)).runDrain.fork
    event = TaskEvent.TaskCreated("task-xyz", "Critical", "team-x", "col-x", "user-x", Instant.now().nn)
    _ <- queue.offer(event)
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.exists(_.message.contains("task-xyz")))

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("runAlertingPipeline function")(
    test("should emit AlertEvent based on TaskEvent")(inMemoryTest),
    test("should emit AlertEvent using runAlertingPipeline")(test),
    test("should not emit alert for empty input")(noAlertOnEmptyInput),
    test("should emit multiple alerts for multiple events")(multipleAlerts),
    test("should emit alerts for duplicate events")(duplicateEvents),
    test("should contain correct content in alert")(alertContentTest),
  ).provideLayer(
    Scope.default >+>
      testLayers
  )
}

object NotifierTestSupport {
  def withCapturedAlerts[R, E, A](
    f: (Notifier, Ref[List[AlertEvent]]) => ZIO[R, E, A]
  ): ZIO[R, E, A] = for {
    ref <- Ref.make(List.empty[AlertEvent])
    notifier = new Notifier {
      def send(message: AlertEvent): Task[Unit] = ref.update(_ :+ message)
    }
    result <- f(notifier, ref)
  } yield result
}

object TestUtils {
  def eventually[R, E, A](effect: ZIO[R, E, Boolean], retries: Int = 20, delay: Duration = 100.millis)
    : ZIO[R, E, Boolean] = {
    effect.flatMap {
      case true => ZIO.succeed(true)
      case false =>
        if (retries <= 0) ZIO.succeed(false)
        else ZIO.sleep(delay) *> eventually(effect, retries - 1, delay)
    }
  }
}
