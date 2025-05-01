package pl.edu.agh.sentinel
package kafka.topics

import zio.Config
import zio.config.magnolia.deriveConfig

case class KafkaTopic(
  name: String,
  partitions: Int = 1,
  replicationFactor: Int = 1,
)

object KafkaTopic {
  given Config[KafkaTopic] = deriveConfig[KafkaTopic]
}
