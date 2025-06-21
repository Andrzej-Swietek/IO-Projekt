package pl.edu.agh.sentinel

import zio.*
import zio.config.typesafe.*
import zio.logging.backend.SLF4J
import zio.redis.Redis
import zio.stream.ZStream

import pl.edu.agh.sentinel.configs.AlertConfig
import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.kafka.{ KafkaEnv, KafkaModule }
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.{ KafkaConsumer, TaskEventConsumer }
import pl.edu.agh.sentinel.kafka.producers.{ AlertEventProducer, KafkaProducer, StatsProducer, UserStatsProducer }
import pl.edu.agh.sentinel.kafka.topics.TopicManager
import pl.edu.agh.sentinel.notifications.{ NotificationEnv, NotificationModule, SentinelNotifier }
import pl.edu.agh.sentinel.processing.{ AlertingEngine, SentinelAlertingEngine, StatsProcessorLive, StatsPublisher }
import pl.edu.agh.sentinel.store.redis.{ RedisEnv, RedisModule }
import pl.edu.agh.sentinel.store.repositories.{ StatsRepository, StatsRepositoryLive }

type SentinelEnv = KafkaEnv & AlertingEngine & RedisEnv & NotificationEnv & StatsRepository

object SentinelApp extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = {
    Runtime.setConfigProvider(
      ConfigProvider.fromResourcePath()
    ) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j
  }

  def runAlertingPipeline(
    taskEventStream: ZStream[Any, Throwable, TaskEvent],
    producer: KafkaProducer,
    engine: AlertingEngine,
  ): ZIO[KafkaEnv & AlertingEngine & NotificationEnv, Throwable, Unit] = for {
    notifier <- ZIO.service[SentinelNotifier]
    // 1. Consume TaskEvent stream from hub
    // 2. Process events through alerting engine, receiving stream of alerts
    alertEventProducer = AlertEventProducer(producer)
    alertEventStream = engine.process(taskEventStream)
    _ <- alertEventStream
      .tap(alert => notifier.send(alert))
      .tap(alert => alertEventProducer.produce("", alert))
      .runDrain
      .retry(Schedule.exponential(1.second))
      .forkDaemon
  } yield ()

  def runStatsPipeline(
    taskEventStream: ZStream[Any, Throwable, TaskEvent],
    producer: KafkaProducer,
  ): ZIO[KafkaEnv & StatsRepository, Throwable, Unit] = for {
    repository <- ZIO.service[StatsRepository]
    statsProcessor = StatsProcessorLive(repository)

    statsPublisher = StatsPublisher(
      UserStatsProducer(producer),
      StatsProducer(producer),
    )
    processedStatsStream = statsProcessor
      .process(taskEventStream)
      .tap(element => ZIO.logInfo(s"Processed stats: $element"))

    _ <- statsPublisher.publish(processedStatsStream).runDrain.forkDaemon
  } yield ()

  private val workflow: ZIO[SentinelEnv, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Starting Sentinel...")

    consumer <- ZIO.service[KafkaConsumer]
    producer <- ZIO.service[KafkaProducer]
    topicManager <- ZIO.service[TopicManager]
    engine <- ZIO.service[AlertingEngine]

    // Ensure all topics are created
    _ <- topicManager.ensureTopicsExist

    hub <- Hub.unbounded[TaskEvent]

    _ <- ZIO.logInfo("Starting alert consumer stream...")
    _ <- TaskEventConsumer(consumer)
      .run
      .tap(event => ZIO.logInfo(s"Incoming TaskEvent: $event"))
      .foreach(hub.publish(_))
      .forkDaemon

    _ <- runAlertingPipeline(ZStream.fromHub(hub), producer, engine)
    _ <- runStatsPipeline(ZStream.fromHub(hub), producer)

    _ <- ZIO.never
  } yield ()

  override val run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] = workflow
    .provide(
      KafkaModule.live,
      AlertConfig.layer >>> SentinelAlertingEngine.layer,
      RedisModule.live,
      NotificationModule.live,
      StatsRepositoryLive.live,
    )
    .catchAll { error =>
      ZIO.debug(s"Error occurred: ${error.getMessage}")
    }
}

// ZIO ENV: KafkaConfig.layer >>> KafkaModule.live ++ (AlertConfig.layer >>> SentinelAlertingEngine.layer) ++ RedisModule.live
