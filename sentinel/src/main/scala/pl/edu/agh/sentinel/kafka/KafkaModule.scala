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

object KafkaModule {

  val live: ZLayer[KafkaConfig & Scope, Throwable, KafkaProducer & KafkaConsumer & KafkaConfig & TopicManager] = {
    KafkaConfig.layer ++
      (for {
        prod <- KafkaProducer.layerFromConfig()
        cons <- KafkaConsumer.layerFromConfig(Earliest)
      } yield prod ++ cons) ++
      TopicManager.layer
  }

  val initialize: ZIO[KafkaConfig, Throwable, Unit] = for {
    config <- ZIO.service[KafkaConfig]
    _ <- ZIO.logInfo(s"Kafka Config: ${config.toString}")
  } yield ()
}
