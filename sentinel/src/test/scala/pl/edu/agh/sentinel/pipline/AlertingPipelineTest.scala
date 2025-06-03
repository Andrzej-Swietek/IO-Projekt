package pl.edu.agh.sentinel.pipline

import zio.*
import zio.kafka.serde.*
import zio.stream.ZStream
import zio.test.*

import pl.edu.agh.sentinel.configs.{DiscordConfig, EmailConfig, NotificationConfig, SlackConfig}
import pl.edu.agh.sentinel.events.{AlertEvent, TaskEvent}
import pl.edu.agh.sentinel.kafka.*
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.ConsumingStrategy.Earliest
import pl.edu.agh.sentinel.kafka.consumers.{ConsumingStrategy, KafkaConsumer, TaskEventConsumer}
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.{KafkaTopic, TopicManager}
import pl.edu.agh.sentinel.notifications.{Notifier, SentinelNotifier}
import pl.edu.agh.sentinel.processing.AlertingEngine


import java.time.Instant

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
    // Simulate the consumer stream
    taskEventStream = ZStream.fromQueue(queue)
    // Simulate the alerting pipeline
    fiber <- engine.process(taskEventStream)
      .tap(alert => notifier.send(alert))
      .runDrain
      .fork
    // Simulate producing an event
    event = TaskEvent.TaskCreated("task-123", "Important", "col-1", "user-1", Instant.now().nn)
    _ <- queue.offer(event)
    _ <- TestClock.adjust(2.seconds)
    alerts <- alertRef.get
    _ <- fiber.interrupt
  } yield assertTrue(alerts.exists(_.message.contains("task-123")))

  def runTestAlertingPipeline(consumer: KafkaConsumer, engine: AlertingEngine)
    : ZIO[SentinelNotifier, Nothing, Fiber.Runtime[Throwable, Unit]] = for {
    notifier <- ZIO.service[SentinelNotifier]

    taskEventStream <- ZIO.succeed(TaskEventConsumer(consumer).run)

    alertEventStream = engine.process(taskEventStream)
    fiber <- alertEventStream
      .tap(alert => notifier.send(alert))
      .runDrain
      .retry(Schedule.exponential(1.second))
      .forkDaemon
  } yield fiber

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("runAlertingPipeline function")(
//    test("runAlertingPipeline should emit AlertEvent based on TaskEvent") {
//      for {
//        env <- ZIO.service[KafkaTestEnv]
//        topicManager <- ZIO.service[TopicManager]
//        producer <- env.getProducer
//        consumer <- env.getConsumer(ConsumingStrategy.Earliest)
//
//        _ <- topicManager.createTopic(topic)
//
//        alertRef <- Ref.make(List.empty[AlertEvent])
//        testNotifier = makeTestNotifier(alertRef)
//
//        fiber <- runTestAlertingPipeline(consumer, engine = TestEngine())
//
//        event = TaskEvent.TaskCreated("task-123", "Important", "col-1", "user-1", Instant.now().nn)
//        _ <- producer.produce(topic.name, "key-1", event)
//
//        _ <- TestClock.adjust(2.seconds)
//        _ <- ZIO.sleep(1.second)
//
//        alerts <- alertRef.get
//        result <- TestUtils.eventually(alertRef.get.map(_.exists(_.message.contains("task-123"))))
//        _ <- fiber.interrupt
//      } yield assertTrue(alerts.exists(_.message.contains("task-123")))
//    } @@ TestAspect.diagnose(3.seconds),

    test("should emit AlertEvent based on TaskEvent")(inMemoryTest)

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
  def eventually[R, E, A](effect: ZIO[R, E, Boolean], retries: Int = 20, delay: Duration = 100.millis): ZIO[R, E, Boolean] =
    effect.flatMap {
      case true  => ZIO.succeed(true)
      case false =>
        if (retries <= 0) ZIO.succeed(false)
        else ZIO.sleep(delay) *> eventually(effect, retries - 1, delay)
    }
}