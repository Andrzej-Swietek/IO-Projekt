package pl.edu.agh.sentinel
package processing

import zio.*
import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.processing.stats.StatsAggregator
import pl.edu.agh.sentinel.store.repositories.StatsRepository
import zio.stream.ZStream


/**
 * TODO: Stats that are to be collected and processed:
 * -[] - Team/Project Level  
 *         - Number of assigned tasks with status: TODO, IN_PROGRESS, DONE
 *         - Active users in X days
 *         - Average time to close ticket  
 *         - Average team member load
 *         - Daily task income  
 * -[] - User Level:
 *         - Number of assigned tasks with status: TODO, IN_PROGRESS, DONE
 *         - Number of closed tasks
 *         - Average time to close ticket  
 *         - Average load
 *         - Performance  
 * Flow: 
 *
 * TaskEventStream
 *    │
 *    ├──▶ AlertingEngine ──▶ AlertEventStream ──▶ Notifier
 *    │
 *    └──▶ StatsProcessor ──▶ Redis ──▶ Core
 *
*/
trait StatsProcessor {
  def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, Nothing]
  // def getStats: Task[CurrentStats] TODO: CurrentStats
  def clean(): Unit
}


case class StatsProcessorLive(repository: StatsRepository) extends StatsProcessor {

  override def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, Nothing] = {
    events
      .groupedWithin(1000, 5.seconds)
      .mapZIO { batch =>
        val teamGrouped = batch.groupBy(_.teamId)
        ZIO.foreachDiscard(teamGrouped) { case (teamId, events) =>
          for {
            maybeOldStats <- repository.getTeamStats(teamId)
            updatedStats <- StatsAggregator.updateStats(maybeOldStats, events)
            _ <- repository.saveTeamStats(updatedStats)
          } yield ()
        }
      }
      .drain
  }

  override def clean(): Unit = ()
}

// TODO : StatsRepository implementation, StatsAggregator implementation, Stats model