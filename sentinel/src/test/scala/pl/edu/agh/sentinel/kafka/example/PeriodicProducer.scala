package pl.edu.agh.sentinel.kafka.example

import zio.*
import zio.json.{ JsonDecoder, JsonEncoder }
import zio.kafka.serde.{ Serde, Serializer }
import zio.stream.ZStream

import pl.edu.agh.sentinel.kafka.producers.KafkaProducer
import pl.edu.agh.sentinel.kafka.topics.SentinelTopics

final case class PeriodicProducer(kafkaProducer: KafkaProducer) {
  given Serializer[Any, String] = Serde.string

  def run: ZIO[Any, Throwable, Unit] = ZStream
    .repeatZIO(Clock.currentDateTime)
    .schedule(Schedule.spaced(60.second))
    .map { time =>
      val key = time.toString
      val value = s"Message at $time"
      (key, value)
    }
    .mapZIO {
      case (key, value) =>
        kafkaProducer.produce(
          SentinelTopics.ManualTestTopic.topicName,
          key,
          value,
        )
    }
    .runDrain
}
