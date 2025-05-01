package pl.edu.agh.sentinel.kafka.example

import zio.ZIO
import zio.kafka.serde.{Deserializer, Serde}

import pl.edu.agh.sentinel.kafka.consumers.KafkaConsumer
import pl.edu.agh.sentinel.kafka.topics.SentinelTopics


final case class PeriodicConsumer(kafkaConsumer: KafkaConsumer) {
  given Deserializer[Any, String] = Serde.string

  def run: ZIO[Any, Nothing, Unit] = kafkaConsumer
    .stream(SentinelTopics.ManualTestTopic.topicName)
    .tap(record => ZIO.logInfo(s"Received record: ${record.value}"))
    .runDrain
    .catchAll { err =>
      ZIO.logError(s"Kafka stream error: ${err.getMessage}")
    }
}