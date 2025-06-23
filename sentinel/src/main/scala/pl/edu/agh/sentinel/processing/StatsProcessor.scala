package pl.edu.agh.sentinel
package processing

import zio._
import zio.stream.ZStream

import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.kafka.producers.{ StatsProducer, UserStatsProducer }
import pl.edu.agh.sentinel.processing.stats.{ StatsAggregator, TeamStats, UserStats }
import pl.edu.agh.sentinel.store.repositories.StatsRepository

trait StatsProcessor {
  def process(events: ZStream[Any, Throwable, TaskEvent]): ZStream[Any, Throwable, (TeamStats, Chunk[UserStats])]
  def clean(): Unit
}

case class StatsProcessorLive(repository: StatsRepository) extends StatsProcessor {
  override def process(events: ZStream[Any, Throwable, TaskEvent])
    : ZStream[Any, Throwable, (TeamStats, Chunk[UserStats])] = {
    events
      .groupedWithin(1000, 5.seconds)
      .mapZIO { batch =>
        val teamGrouped = batch.groupBy(_.teamId)
        ZIO.foreach(teamGrouped) {
          case (teamId, events) =>
            for {
              maybeOldStats <- repository.getTeamStats(teamId)
              updatedStats <- StatsAggregator.updateStats(maybeOldStats, events)
              _ <- repository.saveTeamStats(updatedStats)
              userIds = events.flatMap {
                case TaskEvent.TaskAssigned(_, assigneeId, _, _, _) => Some(assigneeId)
                case TaskEvent.TaskClosed(_, _, _, _, creatorId, _) => Some(creatorId)
                case _ => None
              }.distinct
              userStats <- ZIO.foreach(userIds) { userId =>
                for {
                  maybeOldUserStats <- repository.getUserStats(userId)
                  updatedUserStats = StatsAggregator.updateUserStats(maybeOldUserStats, userId, events)
                  _ <- repository.saveUserStats(updatedUserStats)
                } yield updatedUserStats
              }
            } yield (updatedStats, userStats)
        }
      }
      .mapConcat(identity)
  }

  override def clean(): Unit = for {
    _ <- repository.clean()
  } yield ()
}

final case class StatsPublisher(
  userStatsProducer: UserStatsProducer,
  teamStatsProducer: StatsProducer,
) {
  def publish(statsStream: ZStream[Any, Throwable, (TeamStats, Chunk[UserStats])]): ZStream[Any, Throwable, Nothing] = {
    statsStream.mapZIO {
      case (teamStats, userStatsList) =>
        teamStatsProducer.produce(teamStats.teamId, teamStats) *>
          ZIO.foreachDiscard(userStatsList)(us => userStatsProducer.produce(us.userId, us))
    }.drain
  }
}
