import model.Visitor
import akka.actor.Actor
import akka.actor.ActorRef
import io.circe.generic.auto.exportDecoder
import io.circe.parser.decode

class NotificationHandler(itSupportProcessor: ActorRef, hostProcessor: ActorRef, securityProcessor: ActorRef) extends Actor {

  override def receive: Receive = {
    case message: String =>
      // Deserialize the message into a Visitor object
      decode[Visitor](message) match {
        case Right(visitor) =>
          visitor.status match {
            case "pending" =>
              hostProcessor ! visitor
              securityProcessor ! visitor
            case "checked-in" | "checked-out" | "rejected" =>
              // Notify IT Support, Host, and Security
              itSupportProcessor ! visitor
              hostProcessor ! visitor
              securityProcessor ! visitor
            case _ =>
              println(s"Unknown status: ${visitor.status}")
          }

        case Left(error) =>
          println(s"Failed to decode message: $error")
      }
  }
}
