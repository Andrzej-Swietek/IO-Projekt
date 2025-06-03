package pl.edu.agh.sentinel.processing
package stats

import java.time.Instant

case class TeamStats(
  teamId: String,
  projectId: String,
  members: Set[String],
  memberStats: Map[String, TaskStats],
  dailyTaskIncome: Map[String, Int], // yyyy-MM-dd -> count
  activeUsers: Set[String],
  updatedAt: Instant,
)
