package pl.edu.agh.sentinel
package kafka.producers

import zio.*
import zio.kafka.*
import zio.kafka.producer.*
import zio.kafka.serde.*

import org.apache.kafka.clients.producer.{ ProducerConfig, RecordMetadata }
import pl.edu.agh.sentinel.kafka.config.KafkaConfig

class KafkaProducer(producer: Producer) {
  def produce[K, V](
    topic: String,
    key: K,
    value: V,
  )(using
    keySerializer: Serializer[Any, K],
    valueSerializer: Serializer[Any, V],
  ): Task[RecordMetadata] = producer.produce(topic, key, value, keySerializer, valueSerializer)
}

object KafkaProducer {
  def fromSettings[K, V](settings: ProducerSettings): ZIO[Scope, Throwable, KafkaProducer] = {
    Producer.make(settings).map { producer =>
      new KafkaProducer(producer)
    }
  }

  def layerFromConfig(): ZLayer[Scope, Throwable, KafkaProducer] = {
    ZLayer.fromZIO {
      for {
        cfg <- KafkaConfig.getConfig
        settings = ProducerSettings(cfg.bootstrapServers)
          .withProperty(ProducerConfig.CLIENT_ID_CONFIG, cfg.clientId)
          .withProperties(cfg.producerProperties)
        producer <- fromSettings(settings)
      } yield producer
    }
  }
}
