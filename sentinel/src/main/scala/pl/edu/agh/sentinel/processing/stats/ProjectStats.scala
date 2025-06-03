package pl.edu.agh.sentinel
package processing.stats

import java.time.Instant

case class ProjectStats(projectId: String, tasksByState: Map[String, Int])

case class TaskStats(
  todo: Int,
  inProgress: Int,
  done: Int,
  closedCount: Int,
  avgTimeToClose: Option[Long], // millis
  avgLoad: Double
)
