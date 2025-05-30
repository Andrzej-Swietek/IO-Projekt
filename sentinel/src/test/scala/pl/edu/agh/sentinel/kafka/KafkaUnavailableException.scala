package pl.edu.agh.sentinel.kafka

class KafkaUnavailableException(msg: Option[String] = None, cause: Option[Throwable] = None)
  extends RuntimeException(msg.getOrElse("Kafka is unavailable"), cause.orNull) {

  def this(msg: String) = this(Some(msg), None)

  def this(cause: Throwable) = this(None, Some(cause))

  def this(msg: String, cause: Throwable) = this(Some(msg), Some(cause))
}
