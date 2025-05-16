package pl.edu.agh.sentinel.kafka

import zio.stream.ZStream

trait KafkaRunner[E <: Throwable, R] {
  def run(): ZStream[Any, E, R]
  def name: String
}
