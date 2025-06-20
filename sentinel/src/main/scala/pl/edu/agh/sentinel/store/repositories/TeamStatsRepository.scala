package pl.edu.agh.sentinel.store.repositories

import zio.{ ZIO, ZLayer }
import zio.redis.Redis
import zio.schema.codec.BinaryCodec

import pl.edu.agh.sentinel.processing.stats.{ TeamStats, UserStats }

trait TeamStatsRepository extends RedisRepository[TeamStats] {
  override val keyPrefix: String = "team-stats:"
}

class TeamStatsRepositoryLive(val codec: BinaryCodec[TeamStats], val redis: Redis) extends TeamStatsRepository

object TeamStatsRepository {
  val live: ZLayer[Redis & BinaryCodec[TeamStats], Nothing, TeamStatsRepository] = {
    ZLayer {
      for {
        redis <- ZIO.service[Redis]
        codec <- ZIO.service[BinaryCodec[TeamStats]]
      } yield TeamStatsRepositoryLive(codec, redis)
    }
  }
}
