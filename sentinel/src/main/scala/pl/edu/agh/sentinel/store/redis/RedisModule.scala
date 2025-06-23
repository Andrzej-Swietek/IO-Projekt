package pl.edu.agh.sentinel.store.redis

import zio._
import zio.{ Scope, ZLayer }
import zio.redis.{ AsyncRedis, CodecSupplier, Redis, RedisConfig => RRedisConfig }
import zio.schema.Schema
import zio.schema.codec.{ BinaryCodec, ProtobufCodec }

import scala.util.control.NoStackTrace

type RedisEnv = RedisConfig & Scope.Closeable & ProtobufCodecSupplier.type & Redis & AsyncRedis

object RedisModule {

  val redisScope: ZLayer[RedisConfig, Nothing, Scope.Closeable] = {
    ZLayer.scoped {
      for {
        config <- ZIO.service[RedisConfig]
        scope <- Scope.make
      } yield scope
    }
  }

  val live: ZLayer[Any, Throwable, RedisEnv] = {
    ZLayer
      .make[RedisEnv](
        redisScope,
        RedisConfig.layer,
        ZLayer.succeed(RRedisConfig.Local),
        ProtobufCodecSupplier.layer,
        Redis.singleNode,
      )
      .mapError { e =>
        new RuntimeException(s"Failed to create Redis layer: ${e.getMessage}", e)
      }
  }

}

object ProtobufCodecSupplier extends CodecSupplier {
  def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec

  val layer: ULayer[ProtobufCodecSupplier.type] = ZLayer.succeed(ProtobufCodecSupplier)
}
