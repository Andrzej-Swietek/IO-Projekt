package pl.edu.agh.sentinel.configs

import zio.{ Config, ZIO, ZLayer }
import zio.config.magnolia.deriveConfig
import zio.json.JsonCodec

final case class AlertConfig(
  maxTaskStuckDays: Int = 5,
  maxInactiveDays: Int = 7,
  maxTasksInProgressPerUser: Int = 5,
)

object AlertConfig {
  val config: Config[AlertConfig] = deriveConfig[AlertConfig].nested("alertConfig")

  val getConfig: ZIO[Any, Config.Error, AlertConfig] = ZIO.config[AlertConfig](AlertConfig.config)

  val layer: ZLayer[Any, Throwable, AlertConfig] = ZLayer.fromZIO(ZIO.config[AlertConfig](AlertConfig.config))
}
