package pl.edu.agh.sentinel.kafka

import zio.*

import pl.edu.agh.sentinel.kafka.config.KafkaConfig
import pl.edu.agh.sentinel.kafka.consumers.KafkaConsumer
import pl.edu.agh.sentinel.kafka.topics.{KafkaTopic, StreamProcessingMode}
import pl.edu.agh.sentinel.processing.StatsProcessor

case class StreamSupervisor(handlers: List[KafkaRunner[Throwable, Any]]) {

  def superviseAll: ZIO[Any, Any, Any] =
    ZIO.logInfo("Starting stream supervision...") *>
      ZIO.foreachParDiscard(handlers) { handler =>
        handler.run()
          .tapError(e => ZIO.logError(s"Stream ${handler.name} failed: ${e.getMessage}"))
          .retry(Schedule.exponential(1.second))
          .runDrain
          .forkDaemon
      }
}

//val mySupervisor = StreamSupervisor(List(
//  AlertConsumer(myKafkaConsumer), // więcej handlerów
//  AlertXXConsumer(myKafkaConsumer), // więcej handlerów
//  AlertYYConsumer(myKafkaConsumer), // więcej handlerów
//  AlertZZConsumer(myKafkaConsumer), // więcej handlerów
//))
//mySupervisor.superviseAll