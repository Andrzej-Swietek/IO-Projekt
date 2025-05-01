package pl.edu.agh.sentinel

import pl.edu.agh.sentinel.kafka.KafkaModule
import zio.*
import zio.config.typesafe.*
import zio.logging.backend.SLF4J
import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.KafkaConsumer
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.topics.TopicManager

object SentinelApp extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = {
    Runtime.setConfigProvider(
      ConfigProvider.fromResourcePath()
    ) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j
  }

  private val workflow: ZIO[KafkaConfig & KafkaConsumer & KafkaProducer & TopicManager, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Starting Lesheq...")
    _ <- KafkaModule.initialize
  } yield ()

  override val run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] = workflow
    .provideSomeLayer[ZIOAppArgs & Scope](KafkaConfig.layer >>> KafkaModule.live)
    .catchAll { error =>
      ZIO.debug(s"Error occurred: ${error.getMessage}")
    }
}