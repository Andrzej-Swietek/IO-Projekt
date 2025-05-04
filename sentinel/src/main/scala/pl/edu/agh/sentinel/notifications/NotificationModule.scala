package pl.edu.agh.sentinel.notifications

import zio.*

class NotificationModule {
//  val emailLayer: ZLayer[Any, Throwable, Notifier] =
//    ZLayer.fromFunction { config: EmailConfig =>
//      new EmailNotifier(config.smtpServer, config.smtpPort, config.fromEmail)
//    }
//
//  val slackLayer: ZLayer[Any, Throwable, Notifier] =
//    ZLayer.fromFunction { config: SlackConfig =>
//      new SlackNotifier(config.webhookUrl)
//    }
//
//  val notificationLayer: ZLayer[EmailConfig & SlackConfig, Throwable, Notifier] =
//    emailLayer ++ slackLayer
}
