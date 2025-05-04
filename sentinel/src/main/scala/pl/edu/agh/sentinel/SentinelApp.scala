package pl.edu.agh.sentinel

import zio.*
import zio.config.typesafe.*
import zio.logging.backend.SLF4J

import pl.edu.agh.sentinel.configs.AlertConfig
import pl.edu.agh.sentinel.kafka.KafkaModule
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.{AlertConsumer, KafkaConsumer}
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.topics.TopicManager
import pl.edu.agh.sentinel.processing.{AlertingEngine, SentinelAlertingEngine}

object SentinelApp extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = {
    Runtime.setConfigProvider(
      ConfigProvider.fromResourcePath()
    ) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j
  }

  private val workflow
  : ZIO[KafkaConfig & KafkaConsumer & KafkaProducer & TopicManager & AlertingEngine, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Starting Lesheq...")
    _ <- KafkaModule.initialize

    consumer <- ZIO.service[KafkaConsumer]
    producer <- ZIO.service[KafkaProducer]
    engine <- ZIO.service[AlertingEngine]

    alertConsumerStream <- ZIO.succeed(AlertConsumer(consumer).run)
    alertResultStream <- ZIO.succeed(engine.process(alertConsumerStream)).forkDaemon
    //    streamWithSend = alertEventStream.mapZIO(producer.produce)
    _ <- ZIO.logInfo("Running alerting pipeline...")
    //    _ <- streamWithSend.runDrain.forkDaemon

    _ <- ZIO.logInfo("Starting alert consumer stream...")
  } yield ()

  override val run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] = workflow
    .provideSomeLayer[ZIOAppArgs & Scope](
      KafkaConfig.layer >>> KafkaModule.live ++ (AlertConfig.layer >>> SentinelAlertingEngine.layer)
    )
    .catchAll { error =>
      ZIO.debug(s"Error occurred: ${error.getMessage}")
    }
}
