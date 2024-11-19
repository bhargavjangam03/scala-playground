package actors

import akka.actor._
import models.{GuestInfo}
import com.typesafe.config.ConfigFactory

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Authenticator, Message, MessagingException, PasswordAuthentication, Session, Transport}

class MailActor extends Actor {

  // Load configuration from application.conf
  private val config = ConfigFactory.load()
  private val smtpHost = sys.env.getOrElse("MAIL_SMTP_HOST", config.getString("mail.smtpHost"))
  private val smtpPort = sys.env.getOrElse("MAIL_SMTP_PORT", config.getString("mail.smtpPort"))
  private val senderEmail = sys.env.getOrElse("MAIL_SENDER_EMAIL", config.getString("mail.senderEmail"))
  private val senderName = sys.env.getOrElse("MAIL_SENDER_NAME", config.getString("mail.senderName"))
  private val senderPassword = sys.env.getOrElse("MAIL_SENDER_PASSWORD", config.getString("mail.password"))



  def receive: Receive = {
    case guestInfo: GuestInfo => composeMail(guestInfo)
  }

  def composeMail(guestInfo: GuestInfo): Unit = {
    val body =
      s"""
         |Dear ${guestInfo.name},
         |
         |Welcome! Here are your Wi-Fi access details:
         |
         |WIFI : Originals 5G
         |Password: SNS110067
         |
         |Thank you for visiting us.
         |
         |Best regards,
         |IT Support Team
         |""".stripMargin

    sendEmail(guestInfo.email, "Wi-Fi Details", body)
  }

  def sendEmail(toEmail: String, subject: String, body: String): Unit = {
    val properties = new Properties()
    properties.put("mail.smtp.host", smtpHost)
    properties.put("mail.smtp.port", smtpPort)
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")

    // Create a Session with the email properties and authentication
    val session = Session.getInstance(properties, new Authenticator {
      override def getPasswordAuthentication: PasswordAuthentication =
        new PasswordAuthentication(senderEmail, senderPassword)
    })

    try {
      // Create a new MimeMessage object
      val message = new MimeMessage(session)

      // Set the recipient, sender, subject, and content
      message.setFrom(new InternetAddress(senderEmail, senderName))
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail))
      message.setSubject(subject)
      message.setText(body)

      // Send the email
      Transport.send(message)
      println(s"Email successfully sent to $toEmail")
    } catch {
      case e: MessagingException =>
        println(s"Failed to send email to $toEmail: ${e.getMessage}")
    }
  }
}
