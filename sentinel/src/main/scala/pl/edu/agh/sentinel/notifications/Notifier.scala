package pl.edu.agh.sentinel.notifications

import zio.{ Task, ZIO, ZLayer }

import pl.edu.agh.sentinel.configs.NotificationConfig
import pl.edu.agh.sentinel.events.AlertEvent

trait Notifier {
  def send(message: AlertEvent): Task[Unit]
}

final case class SentinelNotifier(
  notifiers: List[Notifier]
) extends Notifier {
  override def send(message: AlertEvent): Task[Unit] = for {
    _ <- ZIO.foreachPar(notifiers) { notifier =>
      notifier.send(message)
    }
  } yield {
    println(s"Notification sent: $message")
  }
}

object SentinelNotifier {
  val live: ZLayer[NotificationConfig, Throwable, SentinelNotifier] = ZLayer.fromZIO {
    for {
      config <- ZIO.service[NotificationConfig]

      notifiers <- ZIO.attempt {
        val emailNotifier = for {
          smtp <- config.email.smtpServer
          port <- config.email.smtpPort
          from <- config.email.fromAddress
          if config.email.enabled
        } yield EmailNotifier(config.email)

        val slackNotifier = for {
          token <- config.slack.token
          if config.slack.enabled
        } yield SlackNotifier(token)

        val discordNotifier = for {
          webhook <- config.discord.webhookUrl
          if config.discord.enabled
        } yield DiscordNotifier(webhook)

        List(emailNotifier, slackNotifier, discordNotifier).flatten
      }
    } yield SentinelNotifier(notifiers)
  }
}
