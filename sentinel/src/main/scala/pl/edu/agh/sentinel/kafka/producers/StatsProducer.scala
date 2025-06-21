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

final case class StatsProducer(kafkaProducer: KafkaProducer) extends KafkaProducerRunner[Throwable, TeamStats] {

  given Serializer[Any, TeamStats] = ZioJsonSerde[TeamStats]
  given Serializer[Any, String] = Serde.string

  final private val topic: SentinelTopics = SentinelTopics.TeamStats
  final val retryPolicy = RetryPolicy.Spaced3sForever

  override def name: String = "TeamStatsProducer"

  override def produce(key: String, value: TeamStats): ZIO[Any, Throwable, RecordMetadata] = {
    kafkaProducer
      .produce[String, TeamStats](topic.topicName, key, value)
      .retry(retryPolicy.getSchedule)
      .tapBoth(
        err => ZIO.logError(s"Production failed: $err"),
        meta => ZIO.logInfo(s"Sent to ${meta.topic}-${meta.partition}@${meta.offset}"),
      )
  }

  override def produceMany(key: String, values: ZStream[Any, Throwable, TeamStats])
    : ZStream[Any, Throwable, RecordMetadata] = {
    values.mapZIO { value =>
      produce(key, value)
    }
  }
}
