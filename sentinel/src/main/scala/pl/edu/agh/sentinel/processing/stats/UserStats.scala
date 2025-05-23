package pl.edu.agh.sentinel
package processing.stats


case class UserStats(userId: String, assignments: Int, lastActive: Instant)
