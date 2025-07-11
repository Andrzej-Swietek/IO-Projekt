package pl.edu.agh.sentinel.events

import zio.json
import zio.json._
import zio.json.{ DeriveJsonCodec, DeriveJsonDecoder, DeriveJsonEncoder, JsonCodec, JsonDecoder, JsonEncoder }

import java.time.Instant

@jsonDiscriminator("type")
sealed trait TaskEvent {
  def taskId: String;

  def teamId: String;

  def timestamp: Instant
}

object TaskEvent {
  final case class TaskCreated(
    taskId: String,
    title: String,
    teamId: String,
    columnId: String,
    creatorId: String,
    timestamp: Instant,
  ) extends TaskEvent derives JsonCodec

  final case class TaskAssigned(
    taskId: String,
    assigneeId: String,
    teamId: String,
    projectId: String,
    timestamp: Instant,
  ) extends TaskEvent derives JsonCodec

  final case class TaskClosed(
    taskId: String,
    title: String,
    teamId: String,
    columnId: String,
    creatorId: String,
    timestamp: Instant,
  ) extends TaskEvent derives JsonCodec

  final case class TaskMoved(
    taskId: String,
    teamId: String,
    fromColumnId: String,
    toColumnId: String,
    movedBy: String,
    timestamp: Instant,
  ) extends TaskEvent derives JsonCodec

  given JsonCodec[TaskEvent] = DeriveJsonCodec.gen[TaskEvent]
}
