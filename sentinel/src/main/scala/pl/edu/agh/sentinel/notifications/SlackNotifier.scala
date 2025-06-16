package pl.edu.agh.sentinel.notifications

import zio.*
import zio._
import zio.json._

import pl.edu.agh.sentinel.events.AlertEvent
import sttp.client3.*
import sttp.client3._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.quick.backend
import sttp.client3.ziojson.*
import sttp.client3.ziojson._
import sttp.model.Uri

case class SlackPayload(text: String) derives JsonCodec

final case class SlackNotifier(webhookUrl: String) extends Notifier {
  def send(alert: AlertEvent): Task[Unit] = {
    val payload = SlackPayload(s"SENTINEL ALERT\n ${alert.timestamp} - ${alert.severity} n${alert.message}")

    ZIO.scoped {
      HttpClientZioBackend.scoped().flatMap { implicit backend =>
        basicRequest
          .post(uri"$webhookUrl")
          .body(payload)
          .response(asStringAlways)
          .send()
          .flatMap { response =>
            if (response.code.isSuccess) ZIO.unit
            else ZIO.fail(new RuntimeException(s"Slack error: ${response.body}"))
          }
      }
    }
  }
}
