package pl.edu.agh.sentinel.notifications

import zio.Task

trait Notifier {
  def send(message: String): Task[Unit]
}