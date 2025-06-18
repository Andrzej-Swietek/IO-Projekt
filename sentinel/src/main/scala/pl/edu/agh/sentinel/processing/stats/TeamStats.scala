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
  createdCount: Int = 0,
  assignedCount: Int = 0,
  closedCount: Int = 0,
  movedCount: Int = 0,
  statusCounts: Map[String, Int] = Map.empty, // e.g. "TODO" -> 5, "IN_PROGRESS" -> 3, "DONE" -> 2
  avgCloseTimeSeconds: Double = 0.0,
)
