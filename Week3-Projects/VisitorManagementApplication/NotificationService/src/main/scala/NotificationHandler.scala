import model.KafkaMessage
import akka.actor.Actor
import akka.actor.ActorRef
import io.circe.generic.auto.exportDecoder
import io.circe.parser.decode

class NotificationHandler(itSupportProcessor: ActorRef, hostProcessor: ActorRef, securityProcessor: ActorRef) extends Actor {

  override def receive: Receive = {
    case message: String =>
      // Deserialize the message into a Visitor object
      decode[KafkaMessage](message) match {
        case Right(msg) =>
          msg.visitorStatus match {
            case "pending" =>
              hostProcessor ! msg
              securityProcessor ! msg
            case "checked-in" | "checked-out" | "rejected" =>
              // Notify IT Support, Host, and Security
              itSupportProcessor ! msg
              hostProcessor ! msg
              securityProcessor ! msg
            case _ =>
              println(s"Unknown status: ${msg.visitorStatus}")
          }

        case Left(error) =>
          println(s"Failed to decode message: $error")
      }
  }
}
