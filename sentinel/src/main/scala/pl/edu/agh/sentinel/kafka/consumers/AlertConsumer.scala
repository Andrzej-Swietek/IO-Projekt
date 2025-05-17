package pl.edu.agh.sentinel.kafka.consumers

import zio.*
import zio.kafka.serde.{Deserializer, Serde}
import zio.stream.ZStream
import pl.edu.agh.sentinel.events.TaskEvent
import pl.edu.agh.sentinel.kafka.KafkaRunner
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.SentinelTopics

case class AlertConsumer(kafkaConsumer: KafkaConsumer) extends KafkaRunner[Throwable, TaskEvent]  {
  given Deserializer[Any, TaskEvent] = ZioJsonSerde[TaskEvent]
  given Deserializer[Any, String] = Serde.string

  def name: String = s"consumer-${SentinelTopics.Alerts.topicName}"

  def run: ZStream[Any, Throwable, TaskEvent] = kafkaConsumer
    .stream[String, TaskEvent](SentinelTopics.Alerts.topicName)
    .map(_.value)
    .tap(x => ZIO.logInfo(s"Got event: $x"))
    .retry(Schedule.exponential(1.second))
}
