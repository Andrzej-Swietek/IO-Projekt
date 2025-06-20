package pl.edu.agh.sentinel.store.repositories

import zio.*
import zio.redis
import zio.redis.Redis
import zio.schema
import zio.schema.{ DeriveSchema, Schema }
import zio.schema.codec.BinaryCodec
import zio.schema.codec.DecodeError
import zio.stream.ZStream

import pl.edu.agh.sentinel.processing.stats.{ TeamStats, UserStats }
import pl.edu.agh.sentinel.store.redis.ProtobufCodecSupplier

trait StatsRepository {
  def saveTeamStats(stats: TeamStats): UIO[Unit]
  def getTeamStats(teamId: String): UIO[Option[TeamStats]]
  def saveUserStats(stats: UserStats): UIO[Unit]
  def getUserStats(userId: String): UIO[Option[UserStats]]
  def clean(): UIO[Unit]
}

class StatsRepositoryLive(
  teamRepo: TeamStatsRepository,
  userRepo: UserStatsRepository,
) extends StatsRepository {

  override def saveTeamStats(stats: TeamStats): UIO[Unit] =
    teamRepo.save(stats, stats.teamId).orDie

  override def getTeamStats(teamId: String): UIO[Option[TeamStats]] =
    teamRepo.get(teamId).orDie

  override def saveUserStats(stats: UserStats): UIO[Unit] =
    userRepo.save(stats, stats.userId).orDie

  override def getUserStats(userId: String): UIO[Option[UserStats]] =
    userRepo.get(userId).orDie

  override def clean(): UIO[Unit] =
    (teamRepo.getAll *> userRepo.getAll).unit.orDie
}

object StatsRepositoryLive {
  val live: ZLayer[
    Redis & ProtobufCodecSupplier.type,
    Nothing,
    StatsRepository,
  ] = ZLayer {
    for {
      redis <- ZIO.service[Redis]
      codecSupplier <- ZIO.service[ProtobufCodecSupplier.type]

      given Schema[TeamStats] = DeriveSchema.gen[TeamStats]
      given Schema[UserStats] = DeriveSchema.gen[UserStats]

      teamCodec = codecSupplier.get[TeamStats]
      userCodec = codecSupplier.get[UserStats]
      teamRepo = new TeamStatsRepositoryLive(teamCodec, redis)
      userRepo = UserStatsRepositoryLive(userCodec, redis)
    } yield new StatsRepositoryLive(teamRepo, userRepo)
  }
}
