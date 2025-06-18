package pl.edu.agh.sentinel.store.repositories

import pl.edu.agh.sentinel.processing.stats.TeamStats

trait TeamStatsRepository extends RedisRepository[TeamStats] {
  override val keyPrefix: String = "team-stats:"
}
