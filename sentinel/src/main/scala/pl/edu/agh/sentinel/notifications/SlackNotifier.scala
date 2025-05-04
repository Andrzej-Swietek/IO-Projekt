package pl.edu.agh.sentinel.notifications

import zio._
import sttp.client3._
import sttp.client3.quick.backend
import sttp.model.Uri

class SlackNotifier(webhookUrl: String) {

//  def send(message: String): ZIO[Any, Throwable, Unit] = {
//    val request = basicRequest
//      .body(Map("text" -> message))
//      .post(Uri.parse(webhookUrl).getOrElse(throw new IllegalStateException("Invalid webhook URL")))
//
//    ZIO.fromFuture { _ =>
//      request.send(backend).map { response =>
//        if (response.code.isSuccess) {
//          println(s"Slack message sent: $message")
//        } else {
//          println(s"Failed to send Slack message: ${response.body}")
//        }
//      }
//    }
//  }
}
