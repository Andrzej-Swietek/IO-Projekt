package pl.edu.agh.sentinel.store.repositories

import zio.redis.Redis
import zio.schema.codec.BinaryCodec
import pl.edu.agh.sentinel.processing.stats.UserStats
import zio.{ZIO, ZLayer}

trait UserStatsRepository extends RedisRepository[UserStats] {
  override val keyPrefix: String = "user-stats:"
}

case class UserStatsRepositoryLive(val codec: BinaryCodec[UserStats], val redis: Redis) extends UserStatsRepository

object UserStatsRepository {
  val live: ZLayer[Redis & BinaryCodec[UserStats], Nothing, UserStatsRepository] =
    ZLayer {
      for {
        redis <- ZIO.service[Redis]
        codec <- ZIO.service[BinaryCodec[UserStats]]
      } yield UserStatsRepositoryLive(codec, redis)
    }
}