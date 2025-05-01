package pl.edu.agh.sentinel.kafka

trait KafkaRunner[E, R] {
  def run(): Unit
}
