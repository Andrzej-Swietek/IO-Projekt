package pl.edu.agh.sentinel.store.redis

import zio.{ Config, ZIO, ZLayer }
import zio.config.magnolia.deriveConfig
import zio.json.JsonCodec
import zio.redis.RedisError

final case class RedisConfig(
  host: String,
  port: Int,
  password: Option[String],
  ssl: Boolean = false,
  clientName: Option[String] = None,
)

object RedisConfig {

  val config: Config[RedisConfig] =
    deriveConfig[RedisConfig].nested("redis")

  val getConfig: ZIO[Any, Config.Error, RedisConfig] =
    ZIO.config(config)

  val layer: ZLayer[Any, Config.Error, RedisConfig] =
    ZLayer.fromZIO(getConfig)
}
