package pl.edu.agh.sentinel
package processing


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


