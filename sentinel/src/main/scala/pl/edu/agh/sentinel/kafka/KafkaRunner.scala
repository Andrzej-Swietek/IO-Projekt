package pl.edu.agh.sentinel.kafka

import zio._
import org.apache.kafka.clients.producer.RecordMetadata
import zio.stream.ZStream

trait KafkaRunner[E <: Throwable, +R] {
  def run: ZStream[Any, E, R]
  def name: String
}

trait KafkaProducerRunner[E <: Throwable, -R] {
  def name: String
  def produce(key: String, value: R): ZIO[Any, Throwable, RecordMetadata]
  def produceMany(key: String, values: ZStream[Any, E, R]): ZStream[Any, E, RecordMetadata]
}