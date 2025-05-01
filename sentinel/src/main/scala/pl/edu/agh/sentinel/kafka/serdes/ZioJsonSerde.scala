package pl.edu.agh.sentinel
package kafka.serdes

import zio._
import zio.json._
import zio.kafka.serde._

object ZioJsonSerde {
  def apply[A](using JsonEncoder[A], JsonDecoder[A]): Serde[Any, A] = {
    Serde
      .string
      .inmapZIO { json =>
        ZIO
          .fromEither(json.fromJson[A])
          .mapError(err => new RuntimeException(s"Deserialization failed: $err"))
      }(a => ZIO.succeed(a.toJson))
  }
}

extension [A](a: A) {
  def asZioJsonSerde(using encoder: JsonEncoder[A], decoder: JsonDecoder[A]): Serde[Any, A] = ZioJsonSerde[A]
}
