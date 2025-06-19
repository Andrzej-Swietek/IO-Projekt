package pl.edu.agh.sentinel.store.repositories

import zio.*
import zio.redis
import zio.schema
import zio.schema.codec.BinaryCodec
import zio.schema.codec.DecodeError
import zio.stream.ZStream

import pl.edu.agh.sentinel.processing.stats.{ TeamStats, UserStats }

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
