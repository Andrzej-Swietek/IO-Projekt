package pl.edu.agh.sentinel.configs

import zio.{ Config, ZIO, ZLayer }
import zio.config.magnolia.deriveConfig

final case class NotificationConfig(
  enabled: Boolean = false,
  slack: SlackConfig,
  discord: DiscordConfig,
  email: EmailConfig,
)

final case class SlackConfig(enabled: Boolean, token: Option[String])

final case class DiscordConfig(enabled: Boolean, webhookUrl: Option[String])

final case class EmailConfig(
  enabled: Boolean,
  smtpServer: Option[String],
  smtpPort: Option[Int],
  fromAddress: Option[String],
)

object NotificationConfig {
  val config: Config[NotificationConfig] = deriveConfig[NotificationConfig].nested("notifications")

  val getConfig: ZIO[Any, Config.Error, NotificationConfig] = ZIO.config[NotificationConfig](NotificationConfig.config)

  val layer: ZLayer[Any, Throwable, NotificationConfig] =
    ZLayer.fromZIO(ZIO.config[NotificationConfig](NotificationConfig.config))
}
