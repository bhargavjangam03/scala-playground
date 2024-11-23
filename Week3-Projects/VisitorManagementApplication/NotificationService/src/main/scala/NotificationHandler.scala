import model.KafkaMessage
import akka.actor.Actor
import akka.actor.ActorRef

class NotificationHandler(itSupportProcessor: ActorRef, hostProcessor: ActorRef, securityProcessor: ActorRef) extends Actor {

  override def receive: Receive = {
    case msg: KafkaMessage =>{
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

      }
  }
}
