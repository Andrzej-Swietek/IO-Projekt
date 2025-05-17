package pl.edu.agh.sentinel

import zio.*
import zio.config.typesafe.*
import zio.logging.backend.SLF4J
import zio.stream.ZStream
import pl.edu.agh.sentinel.configs.AlertConfig
import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.{AlertConsumer, KafkaConsumer, TaskEventConsumer}
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.topics.TopicManager
import pl.edu.agh.sentinel.kafka.{KafkaEnv, KafkaModule, StreamSupervisor}
import pl.edu.agh.sentinel.notifications.{NotificationEnv, NotificationModule, SentinelNotifier}
import pl.edu.agh.sentinel.processing.{AlertingEngine, SentinelAlertingEngine}
import pl.edu.agh.sentinel.store.redis.{RedisEnv, RedisModule}


type SentinelEnv = KafkaEnv & AlertingEngine & RedisEnv & NotificationEnv

object SentinelApp extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = {
    Runtime.setConfigProvider(
      ConfigProvider.fromResourcePath()
    ) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j
  }

  def runAlertingPipeline(consumer: KafkaConsumer, engine: AlertingEngine): ZIO[KafkaEnv & AlertingEngine & NotificationEnv, Throwable, Unit] = for {
    notifier <- ZIO.service[SentinelNotifier]
    
    // 1. Pobierz strumień task events z konsumenta Kafka
    taskEventStream <- ZIO.succeed(TaskEventConsumer(consumer).run) // lub z environment

    // 2. Przetwórz eventy przez alerting engine, otrzymując stream alertów
    alertEventStream = engine.process(taskEventStream)
    _ <- alertEventStream
      .tap(alert => notifier.send(alert))
      .runDrain
      .retry(Schedule.exponential(1.second))
      .forkDaemon
  } yield ()

  private val workflow: ZIO[SentinelEnv, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Starting Sentinel...")

    consumer <- ZIO.service[KafkaConsumer]
    producer <- ZIO.service[KafkaProducer]
    engine <- ZIO.service[AlertingEngine]


    _ <- ZIO.logInfo("Starting alert consumer stream...")
    _ <- ZIO.never
  } yield ()

  override val run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] = workflow
    .provide(
      KafkaModule.live,
      AlertConfig.layer >>> SentinelAlertingEngine.layer,
      RedisModule.live,
      NotificationModule.live,
    )
    .catchAll { error =>
      ZIO.debug(s"Error occurred: ${error.getMessage}")
    }
}

// ZIO ENV: KafkaConfig.layer >>> KafkaModule.live ++ (AlertConfig.layer >>> SentinelAlertingEngine.layer) ++ RedisModule.live