package pl.edu.agh.sentinel.notifications

import zio.*

import pl.edu.agh.sentinel.configs.NotificationConfig

type NotificationEnv = NotificationConfig & SentinelNotifier

object NotificationModule {

  val notificationScope: ZLayer[NotificationConfig, Nothing, Scope.Closeable] = ZLayer.scoped {
    for {
      config <- ZIO.service[NotificationConfig]
      scope <- Scope.make
    } yield scope
  }

  val live: ZLayer[Any, Throwable, NotificationEnv] = {
    ZLayer
      .make[NotificationEnv](
        NotificationConfig.layer,
        notificationScope,
        SentinelNotifier.live,
      )
      .mapError { e =>
        new RuntimeException(s"Failed to create Notification layer: ${e.getMessage}", e)
      }
  }

}
