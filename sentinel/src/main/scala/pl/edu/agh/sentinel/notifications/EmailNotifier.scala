package pl.edu.agh.sentinel.notifications

import zio._
import sttp.client3._
import sttp.client3.quick.backend
import sttp.model.Uri


class EmailNotifier(smtpServer: String, smtpPort: Int, fromEmail: String)  {
//    override def send(message: String): Task[Unit] = Task {
  //    val properties = new java.util.Properties()
  //    properties.put("mail.smtp.host", smtpServer)
  //    properties.put("mail.smtp.port", smtpPort.toString)
  //    val session = Session.getDefaultInstance(properties)
  //
  //    val mimeMessage = new MimeMessage(session)
  //    mimeMessage.setFrom(new InternetAddress(fromEmail))
  //    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("recipient@example.com"))
  //    mimeMessage.setSubject("Alert Notification")
  //    mimeMessage.setText(message)
  //
  //    Transport.send(mimeMessage)
  //  }
}
