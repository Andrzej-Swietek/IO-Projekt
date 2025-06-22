package pl.edu.agh.sentinel.pipline

import zio.*
import zio.stream.ZStream
import zio.test.{ assertTrue, Spec, TestClock, TestEnvironment, ZIOSpecDefault }
import zio.test.Assertion.*

import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.processing.StatsProcessorLive

object StatsPipelineTest extends ZIOSpecDefault {
  val testEvents: Seq[TaskEvent.TaskCreated] = List(
    TaskEvent.TaskCreated("task-1", "A", "team", "user", "col", java.time.Instant.now().nn),
    TaskEvent.TaskCreated("task-2", "B", "team", "user", "col", java.time.Instant.now().nn),
    TaskEvent.TaskCreated("task-3", "B", "team", "user", "col", java.time.Instant.now().nn),
    TaskEvent.TaskCreated("task-4", "B", "team", "user", "col", java.time.Instant.now().nn),
    TaskEvent.TaskCreated("task-5", "B", "team", "user", "col", java.time.Instant.now().nn),
  )

  val statsTest: Spec[Any, Throwable] = test("StatsProcessorLive emits TeamStats with correct count") {
    for {
      repo <- ZIO.succeed(new InMemoryStatsRepository)
      processor = StatsProcessorLive(repo)
      queue <- Queue.unbounded[TaskEvent]
      _ <- ZIO.foreachDiscard(testEvents)(queue.offer)
      statsFiber <- processor.process(ZStream.fromQueue(queue)).take(1).runCollect.fork
      _ <- TestClock.adjust(6.seconds) // advance clock after stream is running
      _ <- queue.shutdown // shutdown after advancing clock
      stats <- statsFiber.join
    } yield assertTrue(stats.nonEmpty && stats.head._1.createdCount == testEvents.size)
  }

  val emptyInputTest: Spec[Any, Throwable] = test("StatsProcessorLive emits nothing for empty input") {
    for {
      repo <- ZIO.succeed(new InMemoryStatsRepository)
      processor = StatsProcessorLive(repo)
      queue <- Queue.unbounded[TaskEvent]
      statsFiber <- processor.process(ZStream.fromQueue(queue)).take(1).runCollect.fork
      _ <- TestClock.adjust(6.seconds)
      _ <- queue.shutdown
      stats <- statsFiber.join
    } yield assertTrue(stats.isEmpty)
  }

  val multiTeamTest: Spec[Any, Throwable] = test("StatsProcessorLive emits TeamStats for multiple teams") {
    val events = List(
      TaskEvent.TaskCreated("task-1", "A", "team1", "user1", "col", java.time.Instant.now().nn),
      TaskEvent.TaskCreated("task-2", "B", "team2", "user2", "col", java.time.Instant.now().nn),
      TaskEvent.TaskCreated("task-3", "B", "team1", "user1", "col", java.time.Instant.now().nn),
    )
    for {
      repo <- ZIO.succeed(new InMemoryStatsRepository)
      processor = StatsProcessorLive(repo)
      queue <- Queue.unbounded[TaskEvent]
      _ <- ZIO.foreachDiscard(events)(queue.offer)
      statsFiber <- processor.process(ZStream.fromQueue(queue)).take(2).runCollect.fork
      _ <- TestClock.adjust(6.seconds)
      _ <- queue.shutdown
      stats <- statsFiber.join
      teamIds = stats.map(_._1.teamId).toSet
    } yield assertTrue(
      stats.size == 2,
      teamIds == Set("team1", "team2"),
    )
  }

  val duplicateEventsTest: Spec[Any, Throwable] = test("handles duplicate TaskCreated events") {
    val events = List.fill(3)(TaskEvent.TaskCreated("task-1", "A", "team", "user", "col", java.time.Instant.now().nn))
    for {
      repo <- ZIO.succeed(new InMemoryStatsRepository)
      processor = StatsProcessorLive(repo)
      queue <- Queue.unbounded[TaskEvent]
      _ <- ZIO.foreachDiscard(events)(queue.offer)
      statsFiber <- processor.process(ZStream.fromQueue(queue)).take(1).runCollect.fork
      _ <- TestClock.adjust(6.seconds)
      _ <- queue.shutdown
      stats <- statsFiber.join
    } yield assertTrue(stats.nonEmpty && stats.head._1.createdCount == 3)
  }

  val mixedEventTypesTest: Spec[Any, Throwable] = test("ignores non-TaskCreated events") {
    val events = List(
      TaskEvent.TaskCreated("task-1", "A", "team", "user", "col", java.time.Instant.now().nn),
      TaskEvent.TaskMoved("task-1", "team", "user", "col1", "col2", java.time.Instant.now().nn),
    )
    for {
      repo <- ZIO.succeed(new InMemoryStatsRepository)
      processor = StatsProcessorLive(repo)
      queue <- Queue.unbounded[TaskEvent]
      _ <- ZIO.foreachDiscard(events)(queue.offer)
      statsFiber <- processor.process(ZStream.fromQueue(queue)).take(1).runCollect.fork
      _ <- TestClock.adjust(6.seconds)
      _ <- queue.shutdown
      stats <- statsFiber.join
    } yield assertTrue(stats.nonEmpty && stats.head._1.createdCount == 1)
  }

  val noEventsButTimePassesTest: Spec[Any, Throwable] = test("emits nothing if no events and time passes") {
    for {
      repo <- ZIO.succeed(new InMemoryStatsRepository)
      processor = StatsProcessorLive(repo)
      queue <- Queue.unbounded[TaskEvent]
      statsFiber <- processor.process(ZStream.fromQueue(queue)).take(1).runCollect.fork
      _ <- TestClock.adjust(10.seconds)
      _ <- queue.shutdown
      stats <- statsFiber.join
    } yield assertTrue(stats.isEmpty)
  }

  override def spec = suite("StatsPipeline")(
    statsTest,
    emptyInputTest,
    multiTeamTest,
    duplicateEventsTest,
    mixedEventTypesTest,
    noEventsButTimePassesTest,
  )
}
