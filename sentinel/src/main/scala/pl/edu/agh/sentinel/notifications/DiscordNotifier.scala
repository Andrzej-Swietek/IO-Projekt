package pl.edu.agh.sentinel.notifications

import zio.*
import zio.json.*

import pl.edu.agh.sentinel.events.{ AlertEvent, AlertSeverity }
import pl.edu.agh.sentinel.events.AlertEvent.*
import sttp.client3.*
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.ziojson.*

case class DiscordPayload(content: String) derives JsonCodec

final case class DiscordNotifier(webhookUrl: String) extends Notifier {
  def send(alert: AlertEvent): Task[Unit] = {

    val formattedContent = alert.severity match {
      case AlertSeverity.Critical => DiscordNotificationTemplates.highSeverityTemplate(alert)
      case AlertSeverity.Warning => DiscordNotificationTemplates.mediumSeverityTemplate(alert)
      case AlertSeverity.Info => DiscordNotificationTemplates.lowSeverityTemplate(alert)
      case _ => DiscordNotificationTemplates.defaultTemplate(alert)
    }

    val payload = DiscordPayload(formattedContent)

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

object DiscordNotificationTemplates {
  private val prefix = "[SENTINEL] | "
  def defaultTemplate(alert: AlertEvent): String = {
    s"""**📢 $prefix ALERT 📢**
     |**Severity:** `${alert.severity}`
     |**Time:** ${alert.timestamp}
     |
     |${alert.message}
     |""".stripMargin
  }

  def lowSeverityTemplate(alert: AlertEvent): String = {
    s"""**ℹ️ $prefix Info ℹ️**
     |**Severity:** `${alert.severity}`
     |**Time:** ${alert.timestamp}
     |
     |${alert.message}
     |""".stripMargin
  }

  def mediumSeverityTemplate(alert: AlertEvent): String = {
    s"""**⚠️ $prefix Warning ⚠️**
     |**Severity:** `${alert.severity}`
     |**Time:** ${alert.timestamp}
     |
     |${alert.message}
     |
     |Action may be required.
     |""".stripMargin
  }

  def highSeverityTemplate(alert: AlertEvent): String = {
    s"""**🚨🚨🚨 $prefix CRITICAL ALERT 🚨🚨🚨**
     |**🔥 Severity:** `${alert.severity}`
     |**🕒 Time:** ${alert.timestamp}
     |
     |❗ ${alert.message}
     |
     |Please investigate **immediately**.
     |""".stripMargin
  }
}
