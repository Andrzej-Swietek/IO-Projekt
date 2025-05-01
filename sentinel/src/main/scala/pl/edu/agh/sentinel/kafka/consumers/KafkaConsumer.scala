package pl.edu.agh.sentinel
package kafka.consumers

import zio.{Scope, ZIO, ZLayer}
import zio.kafka.consumer.{Consumer, ConsumerSettings, Subscription}
import zio.kafka.serde.Deserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import pl.edu.agh.sentinel.kafka.config.KafkaConfig

class KafkaConsumer(consumer: Consumer) {
  def stream[K, V](
    topic: String
  )(using
    keyDeserializer: Deserializer[Any, K],
    valueDeserializer: Deserializer[Any, V],
  ) =
    consumer.plainStream(Subscription.topics(topic), keyDeserializer, valueDeserializer)
}

object KafkaConsumer {
  def fromSettings(settings: ConsumerSettings): ZIO[Scope, Throwable, KafkaConsumer] = {
    Consumer.make(settings).map { consumer =>
      new KafkaConsumer(consumer)
    }
  }

  def settingsWithOffset(
     cfg: KafkaConfig,
     offsetReset: ConsumingStrategy,
   ): ConsumerSettings = {
    ConsumerSettings(cfg.bootstrapServers)
      .withGroupId(cfg.groupId)
      .withClientId(cfg.clientId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset.strategy)
  }

  def layerFromConfig(
  offsetReset: ConsumingStrategy = ConsumingStrategy.Latest
                     ): ZLayer[Scope, Throwable, KafkaConsumer] = {
    ZLayer.fromZIO {
      for {
        cfg <- KafkaConfig.getConfig
        settings = settingsWithOffset(cfg, offsetReset)
        consumer <- fromSettings(settings)
      } yield consumer
    }
  }
}

enum ConsumingStrategy(autoOffsetReset: String) {
  case Earliest extends ConsumingStrategy("earliest")
  case Latest extends ConsumingStrategy("latest")

  def strategy: String = autoOffsetReset
}