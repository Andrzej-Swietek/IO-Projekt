package pl.edu.agh.sentinel
package kafka.topics

import zio.config.*
import zio.Config
import zio.config.magnolia.deriveConfig
import zio.json.JsonCodec

case class KafkaTopic(
  name: String,
  partitions: Int = 1,
  replicationFactor: Int = 1,
  processingMode: StreamProcessingMode = StreamProcessingMode.RealTime
)

object KafkaTopic {
  given Config[KafkaTopic] = deriveConfig[KafkaTopic]
}

enum StreamProcessingMode derives JsonCodec {
  case RealTime, Batch, Hybrid
}

