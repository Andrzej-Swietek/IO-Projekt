package pl.edu.agh.sentinel.kafka.consumers

import zio.*
import zio.json.*

import zio.kafka.serde.{Deserializer, Serde}
import zio.stream.ZStream
import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.kafka.KafkaRunner
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.SentinelTopics

final case class TaskEventConsumer(kafkaConsumer: KafkaConsumer) extends KafkaRunner[Throwable, TaskEvent] {
  given Deserializer[Any, TaskEvent] = ZioJsonSerde[TaskEvent]
  given Deserializer[Any, String] = Serde.string

  def name: String = s"consumer-${SentinelTopics.TaskEvents.topicName}"
  
//  def run: ZStream[Any, Throwable, TaskEvent] = kafkaConsumer
//    .stream[String, TaskEvent](SentinelTopics.TaskEvents.topicName)
//    .mapZIO(record => record.offset.commit.as(record.value))
//    .tap(x => ZIO.logDebug(s"Got event: $x"))

  def run: ZStream[Any, Throwable, TaskEvent] =
    kafkaConsumer
      .stream[String, String](SentinelTopics.TaskEvents.topicName)
      .mapZIO { record =>
        record.value.fromJson[TaskEvent] match {
          case Right(event) =>
            record.offset.commit.as(Some(event))
          case Left(error) =>
            ZIO.logWarning(s"Deserialization failed: $error, skipping message") *>
              record.offset.commit.as(None)
        }
      }
      .collectSome
      .tap(event => ZIO.logDebug(s"Got event: $event"))
}
