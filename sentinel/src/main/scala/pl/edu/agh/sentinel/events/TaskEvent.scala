package pl.edu.agh.sentinel.events

import java.time.Instant

sealed trait TaskEvent {
  def taskId: String;

  def timestamp: Instant
}


object TaskEvent {
  final case class TaskCreated(taskId: String, title: String, columnId: String, creatorId: String, timestamp: Instant) extends TaskEvent

  final case class TaskClosed(taskId: String, title: String, columnId: String, creatorId: String, timestamp: Instant) extends TaskEvent

  final case class TaskMoved(taskId: String, fromColumnId: String, toColumnId: String, movedBy: String, timestamp: Instant) extends TaskEvent
}