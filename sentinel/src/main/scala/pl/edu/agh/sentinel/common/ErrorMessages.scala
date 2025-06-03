package pl.edu.agh.sentinel
package common

import zio.json.JsonCodec

sealed trait ErrorMessage derives JsonCodec {
  val message: String
}

case class InvalidConfigError(message: String) extends ErrorMessage
case class InvalidTopicError(message: String) extends ErrorMessage
case class InvalidMessageError(message: String) extends ErrorMessage
