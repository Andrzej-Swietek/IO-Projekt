package pl.edu.agh.sentinel.store.repositories

import pl.edu.agh.sentinel.processing.stats.UserStats

trait UserStatsRepository extends RedisRepository[UserStats] {
  override val keyPrefix: String = "user-stats:"
}
