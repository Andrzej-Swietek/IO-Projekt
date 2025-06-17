package pl.edu.agh.sentinel
package processing

import zio.*
import zio.stream.ZStream

import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.processing.stats.StatsAggregator
import pl.edu.agh.sentinel.store.repositories.StatsRepository


trait StatsProcessor {
  def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, Nothing]

  //   def getStats: Task[CurrentStats] TODO: CurrentStats
  def clean(): Unit
}

case class StatsProcessorLive(repository: StatsRepository) extends StatsProcessor {

  override def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, Nothing] = {
    events
      .groupedWithin(1000, 5.seconds)
      .mapZIO { batch =>
        val teamGrouped = batch.groupBy(_.teamId)
        ZIO.foreachDiscard(teamGrouped) {
          case (teamId, events) =>
            for {
              maybeOldStats <- repository.getTeamStats(teamId)
              updatedStats <- StatsAggregator.updateStats(maybeOldStats, events)
              _ <- repository.saveTeamStats(updatedStats)
              // --- User stats update ---
              userIds = events.flatMap {
                case TaskEvent.TaskAssigned(_, assigneeId, _, _, _) => Some(assigneeId)
                case TaskEvent.TaskClosed(_, _, _, _, creatorId, _) => Some(creatorId)
                case _ => None
              }.distinct
              _ <- ZIO.foreachDiscard(userIds) { userId =>
                for {
                  maybeOldUserStats <- repository.getUserStats(userId)
                  updatedUserStats = StatsAggregator.updateUserStats(maybeOldUserStats, userId, events)
                  _ <- repository.saveUserStats(updatedUserStats)
                } yield ()
              }
            } yield ()
        }
      }
      .drain
  }

  override def clean(): Unit = ()
}
