package pl.edu.agh.sentinel.notifications

import zio._
import zio.json._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3._
import sttp.client3.ziojson._
import pl.edu.agh.sentinel.events.AlertEvent


case class DiscordPayload(content: String) derives JsonCodec

final case class DiscordNotifier(webhookUrl: String) extends Notifier {
  def send(alert: AlertEvent): Task[Unit] = {
    val payload = DiscordPayload(
      s"**SENTINEL ALERT**\n${alert.timestamp} - ${alert.severity}\n${alert.message}"
    )

    ZIO.scoped {
      HttpClientZioBackend.scoped().flatMap { implicit backend =>
        basicRequest
          .post(uri"$webhookUrl") // TODO - make this configurable / get from Kanban Core REST
          .body(payload)
          .response(asStringAlways)
          .send()
          .flatMap { response =>
            if (response.code.isSuccess) ZIO.unit
            else ZIO.fail(new RuntimeException(s"Discord error: ${response.body}"))
          }
      }
    }
  }
}