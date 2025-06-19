package pl.edu.agh.sentinel.notifications

import zio.UIO

import pl.edu.agh.sentinel.events.AlertEvent

trait NotificationHub {
  def send(alert: AlertEvent): UIO[Unit]
}
