package pl.edu.agh.sentinel.events

import zio.json.{ DeriveJsonCodec, JsonCodec }

import java.time.Instant

/** * AlertEvent represents an event that triggers an alert in the system. It can be used to notify users about various
  * issues, such as tasks being stuck or users being inactive. It's a result or consequence taken from observations made
  * by the system.
  */
sealed trait AlertEvent {
  def alertId: String

  def message: String

  def timestamp: Instant

  def severity: AlertSeverity
}

object AlertEvent {

  /** AlertEvent for when a task is stuck for too long.
    * This event is triggered when a task is not updated for a specified number of days.
    *
    * @param alertId - Unique identifier for the alert
    * @param taskId - Unique identifier for the task
    * @param title - Title of the task
    * @param userId - Unique identifier for the user
    * @param daysStuck - Number of days the task has been stuck
    * @param message - Message describing the alert
    * @param severity - Severity of the alert (Info, Warning, Critical)
    * @param timestamp - Timestamp of when the alert was generated
    */
  final case class AlertTaskStuck(
    alertId: String,
    taskId: String,
    title: String,
    userId: String,
    daysStuck: Int,
    message: String,
    severity: AlertSeverity,
    timestamp: Instant,
  ) extends AlertEvent derives JsonCodec

  /** AlertEvent for when a user is inactive for too long.
    * This event is triggered when a user has not performed any actions for a specified number of days.
    *
    * @param alertId - Unique identifier for the alert
    * @param userId - Unique identifier for the user
    * @param daysInactive - Number of days the user has been inactive
    * @param message - Message describing the alert
    * @param severity - Severity of the alert (Info, Warning, Critical)
    * @param timestamp - Timestamp of when the alert was generated
    */
  final case class AlertUserInactive(
    alertId: String,
    userId: String,
    daysInactive: Int,
    message: String,
    severity: AlertSeverity,
    timestamp: Instant,
  ) extends AlertEvent derives JsonCodec

  /** AlertEvent for when a team is overloaded with too many tasks.
    * This event is triggered when the number of tasks assigned to a team exceeds a specified threshold.
    *
    * @param alertId - Unique identifier for the alert
    * @param teamId - Unique identifier for the team
    * @param projectId - Unique identifier for the project
    * @param overloadedMembers - Set of user IDs representing overloaded members of the team
    * @param maxRecommended - Maximum recommended number of tasks per member
    * @param message - Message describing the alert
    * @param severity - Severity of the alert (Info, Warning, Critical)
    * @param timestamp - Timestamp of when the alert was generated
    */
  final case class AlertOverloadedTeam(
    alertId: String,
    teamId: String,
    projectId: String,
    overloadedMembers: Set[String],
    maxRecommended: Int,
    message: String,
    severity: AlertSeverity,
    timestamp: Instant,
  ) extends AlertEvent derives JsonCodec

  given JsonCodec[AlertEvent] = DeriveJsonCodec.gen[AlertEvent]
}

enum AlertSeverity derives JsonCodec {
  case Info, Warning, Critical
}
