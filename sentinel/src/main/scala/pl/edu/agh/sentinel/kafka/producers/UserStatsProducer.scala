package pl.edu.agh.sentinel.kafka.producers

import zio.ZIO
import zio.kafka.serde.{ Serde, Serializer }
import zio.stream.ZStream

import org.apache.kafka.clients.producer.RecordMetadata
import pl.edu.agh.sentinel.kafka.KafkaProducerRunner
import pl.edu.agh.sentinel.kafka.RetryPolicy
import pl.edu.agh.sentinel.kafka.serdes.ZioJsonSerde
import pl.edu.agh.sentinel.kafka.topics.SentinelTopics
import pl.edu.agh.sentinel.processing.stats.{ TeamStats, UserStats }

final case class UserStatsProducer(kafkaProducer: KafkaProducer) extends KafkaProducerRunner[Throwable, UserStats] {

  given Serializer[Any, UserStats] = ZioJsonSerde[UserStats]
  given Serializer[Any, String] = Serde.string

  final private val topic: SentinelTopics = SentinelTopics.UserStats
  final val retryPolicy = RetryPolicy.Spaced3sForever

  override def name: String = "UserStatsProducer"

  override def produce(key: String, value: UserStats): ZIO[Any, Throwable, RecordMetadata] = {
    kafkaProducer
      .produce[String, UserStats](topic.topicName, key, value)
      .retry(retryPolicy.getSchedule)
      .tapBoth(
        err => ZIO.logError(s"Production failed: $err"),
        meta => ZIO.logInfo(s"Sent to ${meta.topic}-${meta.partition}@${meta.offset}"),
      )
  }

  override def produceMany(key: String, values: ZStream[Any, Throwable, UserStats])
    : ZStream[Any, Throwable, RecordMetadata] = {
    values.mapZIO { value =>
      produce(key, value)
    }
  }
}
