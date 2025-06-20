package pl.edu.agh.sentinel.kafka.producers

import zio.ZIO
import zio.kafka.serde.{ Serde, Serializer }
import zio.stream.ZStream

import org.apache.kafka.clients.producer.RecordMetadata
import pl.edu.agh.sentinel.events.AlertEvent
import pl.edu.agh.sentinel.kafka.KafkaProducerRunner
import pl.edu.agh.sentinel.kafka.RetryPolicy
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.SentinelTopics

final case class AlertEventProducer(kafkaProducer: KafkaProducer) extends KafkaProducerRunner[Throwable, AlertEvent] {
  given Serializer[Any, AlertEvent] = ZioJsonSerde[AlertEvent]
  given Serializer[Any, String] = Serde.string

  final private val topic: SentinelTopics = SentinelTopics.TeamStats
  final val retryPolicy = RetryPolicy.Spaced3sForever

  override def name: String = "AlertEventProducer"

  override def produce(key: String, value: AlertEvent): ZIO[Any, Throwable, RecordMetadata] = {
    kafkaProducer
      .produce[String, AlertEvent](topic.topicName, key, value)
      .retry(retryPolicy.getSchedule)
      .tapBoth(
        err => ZIO.logError(s"Production failed: $err"),
        meta => ZIO.logInfo(s"Sent to ${meta.topic}-${meta.partition}@${meta.offset}"),
      )
  }

  override def produceMany(key: String, values: ZStream[Any, Throwable, AlertEvent])
    : ZStream[Any, Throwable, RecordMetadata] = {
    values.mapZIO { value =>
      produce(key, value)
    }
  }
}
