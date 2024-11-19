package utils

import models.{Email, Guest, GuestInfo, Menu}
import com.typesafe.config.{Config, ConfigFactory}

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Authenticator, Message, MessagingException, PasswordAuthentication, Session, Transport}

object MailUtil {

  // Load configuration from application.conf
  val config: Config = ConfigFactory.load()

  // Read SMTP configuration from application.conf or environment variables (if set)
  private val smtpHost = sys.env.getOrElse("MAIL_SMTP_HOST", config.getString("mail.smtpHost"))
  private val smtpPort = sys.env.getOrElse("MAIL_SMTP_PORT", config.getString("mail.smtpPort"))
  private val senderEmail = sys.env.getOrElse("MAIL_SENDER_EMAIL", config.getString("mail.senderEmail"))
  private val senderName = sys.env.getOrElse("MAIL_SENDER_Name", config.getString("mail.senderName"))
  private val senderPassword = sys.env.getOrElse("MAIL_SENDER_PASSWORD", config.getString("mail.senderPassword"))

  // Setup mail properties
  val properties: Properties = new Properties()
  properties.put("mail.smtp.host", smtpHost)
  properties.put("mail.smtp.port", smtpPort)
  properties.put("mail.smtp.auth", "true")
  properties.put("mail.smtp.starttls.enable", "true")

  // Create a session with authentication
  val session = Session.getInstance(properties, new Authenticator() {
    override protected def getPasswordAuthentication =
      new PasswordAuthentication(senderEmail, senderPassword)
  })

  def composeMail(guest: GuestInfo, menuList: Seq[Menu]): Email = {
    val listItems = menuList.map { menu =>
      s"<li><strong>${menu.foodItem}</strong> - ${menu.foodType}<br/>Price: ${menu.price}</li>"
    }.mkString("\n")

    val content: String = s"""
                             |<html>
                             |<head>
                             |  <title>Today's Menu for ${guest.name}</title>
                             |  <style>
                             |    body {
                             |      font-family: Arial, sans-serif;
                             |      background-color: #f9f9f9;
                             |      padding: 20px;
                             |    }
                             |    h1, h2 {
                             |      color: #4CAF50;
                             |    }
                             |    ul {
                             |      list-style-type: none;
                             |      padding: 0;
                             |    }
                             |    li {
                             |      background-color: #ffffff;
                             |      margin: 10px 0;
                             |      padding: 10px;
                             |      border-radius: 8px;
                             |      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                             |    }
                             |    p {
                             |      font-size: 14px;
                             |      color: #555555;
                             |    }
                             |    .footer {
                             |      font-size: 12px;
                             |      color: #888888;
                             |      text-align: center;
                             |      margin-top: 20px;
                             |    }
                             |  </style>
                             |</head>
                             |<body>
                             |  <h1>Good Morning ${guest.name},</h1>
                             |  <h2>Today's Menu at Our Restaurant</h2>
                             |  <ul>
                             |    $listItems
                             |  </ul>
                             |  <p>We hope you enjoy your meal today. Feel free to reach out if you have any questions!</p>
                             |  <p>Best Regards,</p>
                             |  <p>The Originals Team</p>
                             |  <div class="footer">
                             |    <p>Follow us on social media for more updates!</p>
                             |  </div>
                             |</body>
                             |</html>
    """.stripMargin

    Email(guest.email, s"Today's Menu for ${guest.name}", content)
  }

  def composeAndSendEmail(guestInfo: GuestInfo, menu: Seq[Menu]): Unit = {
    val mailContent = composeMail(guestInfo, menu)
    sendEmail(mailContent)
  }

  def composeAndSendEmailAllGuests(guestList: Seq[Guest], menu: Seq[Menu]): Unit = {
    guestList.foreach(guest => composeAndSendEmail(GuestInfo(guest.name, guest.email), menu))
  }

  private def sendEmail(email: Email): Unit = {
    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(senderEmail, senderName))
      message.setRecipients(Message.RecipientType.TO, email.receiverId)
      message.setSubject(email.subject)
      message.setContent(email.body, "text/html; charset=utf-8")
      Transport.send(message)
      println(s"Email sent to ${email.receiverId}")
    } catch {
      case e: MessagingException =>
        e.printStackTrace()
    }
  }
}
