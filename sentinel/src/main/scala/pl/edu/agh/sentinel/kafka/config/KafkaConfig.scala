package pl.edu.agh.sentinel
package kafka.config

import zio.{ Config, ZIO, ZLayer }
import zio.config.magnolia.deriveConfig
import zio.json.JsonCodec

import pl.edu.agh.sentinel.kafka.topics.KafkaTopic

type ClientId = String
type GroupId = String
type BootstrapServer = String

case class KafkaConfig(
  bootstrapServers: List[BootstrapServer],
  groupId: GroupId,
  clientId: ClientId,
  autoOffsetReset: String,
  topics: List[KafkaTopic],
  producerProperties: Map[String, String],
)



object KafkaConfig {

  val config: Config[KafkaConfig] = deriveConfig[KafkaConfig].nested("kafka")

  val getConfig: ZIO[Any, Config.Error, KafkaConfig] = ZIO.config[KafkaConfig](KafkaConfig.config)

  val layer: ZLayer[Any, Throwable, KafkaConfig] = ZLayer.fromZIO(ZIO.config[KafkaConfig](KafkaConfig.config))
}
