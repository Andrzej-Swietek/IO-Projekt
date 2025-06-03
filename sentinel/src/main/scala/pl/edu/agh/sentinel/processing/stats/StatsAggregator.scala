package pl.edu.agh.sentinel.processing.stats

import java.time.Instant

import pl.edu.agh.sentinel.events.TaskEvent

object StatsAggregator {
  def updateStats(
    maybeOldStats: Option[TeamStats],
    events: Seq[TaskEvent],
  ): zio.Task[TeamStats] = {
    
    // just update the updatedAt timestamp and increment dailyTaskIncome
    val now = Instant.now()
    val oldStats = maybeOldStats.getOrElse(
      TeamStats(
        teamId = events.headOption.map(_.teamId).getOrElse(""),
        projectId = events.headOption.map(_.projectId).getOrElse(""),
        members = Set.empty,
        memberStats = Map.empty,
        dailyTaskIncome = Map.empty,
        activeUsers = Set.empty,
        updatedAt = now.nn,
      )
    )

    // count new tasks per day
    val newDailyIncome = events.groupBy(e => e.timestamp.toString.substring(0, 10)).view.mapValues(_.size).toMap
    val mergedDailyIncome = oldStats.dailyTaskIncome ++ newDailyIncome.map {
      case (k, v) =>
        k.nn -> (oldStats.dailyTaskIncome.getOrElse(k.nn, 0) + v).nn
    }

    zio
      .ZIO
      .succeed(
        oldStats.copy(
          dailyTaskIncome = mergedDailyIncome.nn,
          updatedAt = now.nn,
        )
      )
  }
}
