package pl.edu.agh.sentinel
package processing.stats

import java.time.Instant

case class UserStats(userId: String, assignments: Int, lastActive: Instant)
