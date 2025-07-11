package pl.edu.agh.sentinel
package processing

import zio.*
import zio.{ Clock, UIO }
import zio.stream.ZStream

import java.time.Duration as JDuration
import java.time.Instant

import pl.edu.agh.sentinel.configs.AlertConfig
import pl.edu.agh.sentinel.events.{ AlertEvent, AlertSeverity, TaskEvent }

trait AlertingEngine {
  def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, AlertEvent]
}

final case class SentinelAlertingEngine(
  config: AlertConfig,
  userActivityRef: Ref.Synchronized[Map[String, Instant]],
  taskStatusRef: Ref.Synchronized[Map[String, (String, String, Instant)]],
  teamTasksRef: Ref.Synchronized[Map[(String, String), Map[String, Int]]],
  now: UIO[Instant] = Clock.instant,
) extends AlertingEngine {

  override def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, AlertEvent] = {
    events
      .tap(event => ZIO.logDebug(s"Processing event: $event"))
      .groupedWithin(1000, 5.seconds)
      .mapZIO { batch =>
        for {
          now <- this.now
          _ <- ZIO.foreachDiscard(batch)(event => updateState(event, now))
          alerts <- ZIO.foreach(batch)(event => detectAlerts(event, now))
        } yield alerts.flatten
      }
      .mapConcat(identity)
  }

  private def updateState(event: TaskEvent, now: Instant): UIO[Unit] = event match {
    case TaskEvent.TaskCreated(_, taskId, _, _, creatorId, _) =>
      for {
        _ <- userActivityRef.update(_.updated(creatorId, now))
        _ <- taskStatusRef.update(_.updated(taskId, ("TODO", creatorId, now)))
      } yield ()

    case TaskEvent.TaskMoved(_, taskId, _, to, movedBy, _) =>
      for {
        _ <- userActivityRef.update(_.updated(movedBy, now))
        _ <- taskStatusRef.update(old => {
          old.get(taskId) match {
            case Some((_, userId, createdAt)) => old.updated(taskId, (to, userId, now))
            case None => old
          }
        })
      } yield ()

    case TaskEvent.TaskClosed(_, taskId, _, closedBy, _, _) =>
      for {
        _ <- userActivityRef.update(_.updated(closedBy, now))
        _ <- taskStatusRef.update(_ - taskId)
      } yield ()

    case TaskEvent.TaskAssigned(_, taskId, assigneeId, teamId, projectId) =>
      teamTasksRef.update { old =>
        val key = (teamId, projectId.toString)
        val currentTeamMap = old.getOrElse(key, Map.empty)
        val updatedUserTasks = currentTeamMap.updatedWith(assigneeId) {
          case Some(count) => Some(count + 1)
          case None => Some(1)
        }
        old.updated(key, updatedUserTasks)
      }
  }

  private def detectAlerts(event: TaskEvent, now: Instant): UIO[List[AlertEvent]] = {
    val checkTaskStuck = taskStatusRef
      .get
      .map(_.collect {
        case (taskId, ("IN_PROGRESS", userId, lastMoved)) if daysBetween(lastMoved, now) >= config.maxTaskStuckDays =>
          AlertEvent.AlertTaskStuck(
            alertId = s"task-stuck-$taskId",
            taskId = taskId,
            title = s"Task $taskId stuck",
            userId = userId,
            daysStuck = daysBetween(lastMoved, now),
            message = s"Task $taskId has been stuck for ${daysBetween(lastMoved, now)} days",
            severity = AlertSeverity.Warning,
            timestamp = now,
          )
      }.toList)

    val checkUserInactive = userActivityRef
      .get
      .map(_.collect {
        case (userId, lastSeen) if daysBetween(lastSeen, now) >= config.maxInactiveDays =>
          AlertEvent.AlertUserInactive(
            alertId = s"user-inactive-$userId",
            userId = userId,
            daysInactive = daysBetween(lastSeen, now),
            message = s"User $userId has been inactive for ${daysBetween(lastSeen, now)} days",
            severity = AlertSeverity.Warning,
            timestamp = now,
          )
      }.toList)

    val checkOverloadedTeams = teamTasksRef
      .get
      .map(_.collect {
        case ((teamId, projectId), userTaskCounts) =>
          val overloaded = userTaskCounts.filter {
            case (_, count) => count > config.maxTasksInProgressPerUser
          }.keySet
          if overloaded.nonEmpty then
            Some(
              AlertEvent.AlertOverloadedTeam(
                alertId = s"team-overload-$teamId",
                teamId = teamId,
                projectId = projectId,
                overloadedMembers = overloaded,
                maxRecommended = config.maxTasksInProgressPerUser,
                message = s"Team $teamId is overloaded",
                severity = AlertSeverity.Warning,
                timestamp = now,
              )
            )
          else None
      }.flatten.toList)

    for {
      a1 <- checkTaskStuck
      a2 <- checkUserInactive
      a3 <- checkOverloadedTeams
    } yield a1 ++ a2 ++ a3
  }

  private def daysBetween(from: Instant, to: Instant): Int =
    JDuration.between(from, to).nn.toDays.toInt
}

object SentinelAlertingEngine {
  val layer: ZLayer[AlertConfig, Nothing, AlertingEngine] = {
    ZLayer.scoped {
      for {
        now <- Clock.instant
        config <- ZIO.service[AlertConfig]
        userActivityRef <- Ref.Synchronized.make(Map.empty[String, Instant])
        taskStatusRef <- Ref.Synchronized.make(Map.empty[String, (String, String, Instant)])
        teamTasksRef <- Ref.Synchronized.make(Map.empty[(String, String), Map[String, Int]])
      } yield SentinelAlertingEngine(config, userActivityRef, taskStatusRef, teamTasksRef)
    }
  }
}
