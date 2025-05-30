package pl.edu.agh.sentinel
package processing.stats

case class ProjectStats(projectId: String, tasksByState: Map[String, Int])
