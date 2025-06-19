package pl.edu.agh.sentinel.notifications

import zio.*

import java.util.Properties
import javax.mail.*
import javax.mail.internet.*

import pl.edu.agh.sentinel.configs.EmailConfig
import pl.edu.agh.sentinel.events.AlertEvent
import sttp.client3.*
import sttp.client3.quick.backend
import sttp.model.Uri

final case class EmailNotifier(config: EmailConfig) extends Notifier {
  def send(event: AlertEvent): Task[Unit] = ZIO.attemptBlocking {
    if (config.enabled && config.smtpServer.isDefined && config.fromAddress.isDefined) {
      val props = new Properties()
      props.put("mail.smtp.host", config.smtpServer)
      props.put("mail.smtp.port", config.smtpPort.toString)
      val session = Session.getInstance(props, null)

      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(config.fromAddress.get))
      val toAddresses = List("aswietek@student.agh.edu.pl") // TODO - make this configurable / get from Kanban Core REST
      toAddresses.foreach(to => message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)))
      message.setSubject(s"[${event.severity}] ${event.timestamp}")
      message.setText(event.message)

      Transport.send(message)
    }
  }
}
