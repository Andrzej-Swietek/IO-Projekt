package pl.edu.agh.sentinel.kafka.producers

import org.apache.kafka.clients.producer.RecordMetadata
import pl.edu.agh.sentinel.kafka.KafkaProducerRunner
import pl.edu.agh.sentinel.processing.stats.TeamStats
import zio.ZIO
import zio.stream.ZStream

final case class StatsProducer(kafkaProducer: KafkaProducer) extends KafkaProducerRunner[Throwable, TeamStats]{

  
  override def name: String = "TeamStatsProducer"

  override def produce(key: String, value: TeamStats): ZIO[Any, Throwable, RecordMetadata] = ???


  override def produceMany(key: String, values: ZStream[Any, Throwable, TeamStats]): ZStream[Any, Throwable, RecordMetadata] = ???
}
