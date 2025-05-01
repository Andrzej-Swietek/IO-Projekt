package pl.edu.agh.sentinel.kafka

import zio.json.{ JsonDecoder, JsonEncoder }

final case class KafkaTestEntity(
  groups: Option[Set[String]] = Option.empty,
  values: Map[String, Double] = Map.empty,
) derives JsonDecoder,
    JsonEncoder
