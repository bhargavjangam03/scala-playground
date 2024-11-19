import model.Visitor
import akka.actor.Actor

class ITSupportProcessor extends Actor {
  override def receive: Receive = {
    case visitor: Visitor =>
      val name = visitor.name
      val email = visitor.email

      visitor.status match {
        case "checked-in" =>
          // Define Wi-Fi access email content
          val subject = s"Wi-Fi Access Details for $name"
          val body =
            s"""
               |Dear $name,
               |
               |Welcome! Here are your Wi-Fi access details:
               |
               |WIFI : WAVEROCK
               |Password: WaveRock1234
               |
               |Thank you for visiting us.
               |
               |Best regards,
               |IT Support Team
               |""".stripMargin

          // Send Wi-Fi details email
          EmailUtils.sendEmail(email, subject, body)
          println(s"Wi-Fi details email sent to $email successfully.")

        case "checked-out" =>
          // Define exit notification email content
          val subject = s"Exit Confirmation for $name"
          val body =
            s"""
               |Dear $name,
               |
               |We hope you had a pleasant visit. This email confirms your check-out.
               |
               |Thank you for visiting us.
               |
               |Best regards,
               |IT Support Team
               |""".stripMargin

          // Send exit confirmation email
          EmailUtils.sendEmail(email, subject, body)
          println(s"Exit confirmation email sent to $email successfully.")
        case "rejected" =>
          // Define rejection notification email content
          val subject = s"Entry Rejection Notification for $name"
          val body =
            s"""
               |Dear $name,
               |
               |We regret to inform you that your entry request has been rejected.
               |
               |If you believe this is a mistake, please contact our support team for assistance.
               |
               |Thank you for your understanding.
               |
               |Best regards,
               |Security Team
               |""".stripMargin

          // Send rejection notification email
          EmailUtils.sendEmail(email, subject, body)
          println(s"Entry rejection email sent to $email successfully.")


        case _ =>
          println(s"Unknown visitor status: ${visitor.status}")
      }
  }
}

class HostProcessor extends Actor {
  override def receive: Receive = {
    case visitor: Visitor =>
      val name = visitor.name
      val hostName = visitor.hostName
      val contactNumber = visitor.contactNumber
      val hostMail = visitor.hostMail
      val building = visitor.building

      visitor.status match {
        case "pending" =>
          // Define visitor arrival notification for approval
          val subject = "Visitor Arrival Notification"
          val approvalLink = s"http://localhost:9000/api/visitor/approve/${visitor.visitorId.get}"
          val rejectionLink = s"http://localhost:9000/api/visitor/reject/${visitor.visitorId.get}"
          val body =
            s"""
               |Dear $hostName,
               |
               |Your visitor, $name, has arrived.
               |
               |Contact Number: $contactNumber
               |Building: $building
               |
               |To allow entry, click here: $approvalLink
               |To deny entry, click here: $rejectionLink
               |
               |Best regards,
               |Visitor Management System
               |""".stripMargin

          // Send arrival notification email to host
          EmailUtils.sendEmail(hostMail, subject, body)
          println(s"Visitor arrival notification sent to host at $hostMail for visitor $name.")

        case "checked-in" =>
          val subject = "Visitor Check-in Confirmation"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is to inform you that your visitor, $name, has successfully checked in.
               |
               |Best regards,
               |Visitor Management System
               |""".stripMargin

          // Send check-in confirmation email to host
          EmailUtils.sendEmail(hostMail, subject, body)
          println(s"Visitor check-in confirmation sent to host at $hostMail for visitor $name.")

        case "checked-out" =>
          val subject = "Visitor Check-out Notification"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is to inform you that your visitor, $name, has checked out.
               |
               |Thank you for using our service.
               |
               |Best regards,
               |Visitor Management System
               |""".stripMargin

          // Send check-out notification email to host
          EmailUtils.sendEmail(hostMail, subject, body)
          println(s"Visitor check-out notification sent to host at $hostMail for visitor $name.")

        case "rejected" =>
          // Define rejection confirmation for the host
          val subject = s"Visitor Entry Rejection Confirmation for $name"
          val body =
            s"""
               |Dear $hostName,
               |
               |This is to confirm that the entry request for your visitor, $name, has been rejected successfully.
               |
               |Thank you for using the Visitor Management System.
               |
               |Best regards,
               |Visitor Management System
               |""".stripMargin

          // Send rejection confirmation email to the host
          EmailUtils.sendEmail(hostMail, subject, body)
          println(s"Visitor entry rejection confirmation sent to host at $hostMail for visitor $name.")

        case _ =>
          println(s"Unknown visitor status: ${visitor.status}")
      }
  }
}

class SecurityProcessor extends Actor {
  override def receive: Receive = {
    case visitor: Visitor =>
      visitor.status match {
        case "checked-in" =>
          println(s"Security Team notified: Visitor ${visitor.name} has checked in.")

        case "checked-out" =>
          println(s"Security Team notified: Visitor ${visitor.name} has checked out.")

        case "pending" =>
          println(s"Security Team notified: Visitor ${visitor.name} is awaiting host confirmation.")

        case "rejected" =>
          println(s"Security Team notified: Visitor ${visitor.name} has been rejected.")

        case _ =>
          println(s"Unknown visitor status: ${visitor.status}")
      }
  }
}
