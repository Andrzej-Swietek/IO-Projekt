package pl.edu.agh.sentinel
package kafka

import zio.*
import zio.config.typesafe.*
import zio.logging.backend.SLF4J

import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.ConsumingStrategy.Earliest
import pl.edu.agh.sentinel.kafka.consumers.KafkaConsumer
import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.topics.TopicManager

type KafkaEnv = KafkaConfig & KafkaProducer & KafkaConsumer & TopicManager

object KafkaModule {

  val kafkaScope: ZLayer[KafkaConfig, Nothing, Scope.Closeable] = ZLayer.fromZIO {
    for {
      config <- ZIO.service[KafkaConfig]
      _ <- ZIO.logInfo(s"Kafka Config: ${config.toString}")
      _ <- ZIO.logInfo("Kafka Module initialized")
      scope <- Scope.make
    } yield scope
  }

  val live: ZLayer[Any, Throwable, KafkaEnv] = {
    ZLayer
      .make[KafkaEnv](
        kafkaScope,
        KafkaConfig.layer,
        KafkaProducer.layerFromConfig(),
        KafkaConsumer.layerFromConfig(Earliest),
        TopicManager.layer,
      )
      .mapError { e =>
        new RuntimeException(s"Failed to create Kafka layer: ${e.getMessage}", e)
      }
  }
}
