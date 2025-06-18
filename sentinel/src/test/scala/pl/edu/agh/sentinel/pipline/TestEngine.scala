package pl.edu.agh.sentinel.pipline

import zio.stream.ZStream

import java.time.Instant

import pl.edu.agh.sentinel.events.{ AlertEvent, AlertSeverity, TaskEvent }
import pl.edu.agh.sentinel.processing.AlertingEngine

class TestEngine extends AlertingEngine {
  override def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, AlertEvent] = {
    events.map(te => {
      AlertEvent.AlertTaskStuck(
        alertId = java.util.UUID.randomUUID().toString,
        taskId = te.taskId,
        title = te match {
          case TaskEvent.TaskCreated(_, title, _, _, _) => title
          case _ => "Unknown"
        },
        userId = te match {
          case TaskEvent.TaskCreated(_, _, _, creatorId, _) => creatorId
          case TaskEvent.TaskAssigned(_, assigneeId, _, _, _) => assigneeId
          case _ => "system"
        },
        daysStuck = 5,
        message = s"Task ${te.taskId} is stuck",
        severity = AlertSeverity.Warning,
        timestamp = Instant.parse("2023-10-01T00:00:00Z").nn,
      )
    })
  }
}
