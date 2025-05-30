package pl.edu.agh.sentinel.notifications

import pl.edu.agh.sentinel.events.AlertEvent
import zio.UIO


trait NotificationHub {
  def send(alert: AlertEvent): UIO[Unit]
}