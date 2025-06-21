package pl.edu.agh.sentinel
package processing.stats

import zio.json.JsonCodec

import java.time.Instant

case class UserStats(
  userId: String,
  assignments: Int = 0,
  closed: Int = 0,
  lastActive: Instant = Instant.EPOCH.nn,
  statusCounts: Map[String, Int] = Map.empty,
  avgCloseTimeSeconds: Double = 0.0,
) derives JsonCodec
