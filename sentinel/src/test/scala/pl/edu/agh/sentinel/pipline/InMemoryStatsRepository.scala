package pl.edu.agh.sentinel.pipline

import pl.edu.agh.sentinel.processing.stats.{TeamStats, UserStats}
import pl.edu.agh.sentinel.store.repositories.StatsRepository
import zio.ZIO

class InMemoryStatsRepository extends StatsRepository {
  private val teamStats = scala.collection.mutable.Map.empty[String, TeamStats]
  private val userStats = scala.collection.mutable.Map.empty[String, UserStats]

  def getTeamStats(teamId: String) = ZIO.succeed(teamStats.get(teamId))
  def saveTeamStats(stats: TeamStats) = ZIO.succeed(teamStats.update(stats.teamId, stats))
  def getUserStats(userId: String) = ZIO.succeed(userStats.get(userId))
  def saveUserStats(stats: UserStats) = ZIO.succeed(userStats.update(stats.userId, stats))
  def clean() = ZIO.succeed { teamStats.clear(); userStats.clear() }
}
