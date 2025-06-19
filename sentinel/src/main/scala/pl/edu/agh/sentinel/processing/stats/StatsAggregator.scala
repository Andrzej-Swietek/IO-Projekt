package pl.edu.agh.sentinel.processing.stats

import java.time.Instant

import pl.edu.agh.sentinel.events.TaskEvent
object StatsAggregator {

  def updateStats(
    maybeOldStats: Option[TeamStats],
    events: Seq[TaskEvent],
  ): zio.Task[TeamStats] = {
    val now = Instant.now()
    val oldStats = maybeOldStats.getOrElse(
      TeamStats(
        teamId = events.headOption.map(_.teamId).getOrElse(""),
        projectId = events
          .collectFirst {
            case TaskEvent.TaskAssigned(_, _, _, projectId, _) => projectId
          }
          .getOrElse(""),
        members = Set.empty,
        memberStats = Map.empty,
        dailyTaskIncome = Map.empty,
        activeUsers = Set.empty,
        updatedAt = now.nn,
      )
    )

    // Daily task income
    val newDailyIncome = events.groupBy(e => e.timestamp.toString.substring(0, 10)).view.mapValues(_.size).toMap
    val mergedDailyIncome = oldStats.dailyTaskIncome ++ newDailyIncome.map {
      case (k, v) =>
        k.nn -> (oldStats.dailyTaskIncome.getOrElse(k.nn, 0) + v).nn
    }

    // Event type counts
    val createdCount = oldStats.createdCount + events.count(_.isInstanceOf[TaskEvent.TaskCreated])
    val assignedCount = oldStats.assignedCount + events.count(_.isInstanceOf[TaskEvent.TaskAssigned])
    val closedCount = oldStats.closedCount + events.count(_.isInstanceOf[TaskEvent.TaskClosed])
    val movedCount = oldStats.movedCount + events.count(_.isInstanceOf[TaskEvent.TaskMoved])

    // Active users (all event types)
    val activeUsers = oldStats.activeUsers ++ events.flatMap {
      case TaskEvent.TaskCreated(_, _, _, _, creatorId, _) => Some(creatorId)
      case TaskEvent.TaskAssigned(_, assigneeId, _, _, _) => Some(assigneeId)
      case TaskEvent.TaskClosed(_, _, _, _, creatorId, _) => Some(creatorId)
      case TaskEvent.TaskMoved(_, _, _, _, movedBy, _) => Some(movedBy)
      case _ => None
    }

    // Status counts (not available in your current model, placeholder for future extension)
    val statusCounts: Map[String, Int] = Map.empty
    // If you add status to events, fill this map as needed

    // Average close time (not possible without task creation time in events, placeholder)
    val avgCloseTimeSeconds: Double = oldStats.avgCloseTimeSeconds
    // If you add creation time to TaskClosed, you can compute average close time

    zio
      .ZIO
      .succeed(
        oldStats.copy(
          dailyTaskIncome = mergedDailyIncome.nn,
          updatedAt = now.nn,
          createdCount = createdCount,
          assignedCount = assignedCount,
          closedCount = closedCount,
          movedCount = movedCount,
          activeUsers = activeUsers.toSet,
          statusCounts = statusCounts,
          avgCloseTimeSeconds = avgCloseTimeSeconds,
        )
      )
  }

  def updateUserStats(
    maybeOldStats: Option[UserStats],
    userId: String,
    events: Seq[TaskEvent],
  ): UserStats = {
    val assignments = events.count {
      case TaskEvent.TaskAssigned(_, assigneeId, _, _, _) if assigneeId == userId => true
      case _ => false
    }
    val closed = events.count {
      case TaskEvent.TaskClosed(_, _, _, _, creatorId, _) if creatorId == userId => true
      case _ => false
    }
    val lastActive = events
      .filter {
        case TaskEvent.TaskAssigned(_, assigneeId, _, _, _) if assigneeId == userId => true
        case TaskEvent.TaskClosed(_, _, _, _, creatorId, _) if creatorId == userId => true
        case _ => false
      }
      .map(_.timestamp)
      .sortWith(_.isAfter(_))
      .headOption
      .getOrElse(maybeOldStats.map(_.lastActive).getOrElse(Instant.EPOCH))

    // Status counts (not available in your current model, placeholder)
    val statusCounts: Map[String, Int] = Map.empty

    // Average close time (not possible without creation time, placeholder)
    val avgCloseTimeSeconds: Double = maybeOldStats.map(_.avgCloseTimeSeconds).getOrElse(0.0)

    val old = maybeOldStats.getOrElse(UserStats(userId))
    old.copy(
      assignments = old.assignments + assignments,
      closed = old.closed + closed,
      lastActive = lastActive.nn,
      statusCounts = statusCounts,
      avgCloseTimeSeconds = avgCloseTimeSeconds,
    )
  }
}
