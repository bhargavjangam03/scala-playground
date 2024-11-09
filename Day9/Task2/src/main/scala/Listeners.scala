import akka.actor.{Actor, ActorRef}

class CloudListener(messageGatherer: ActorRef) extends Actor {
  def receive: Receive = {
    case msg: Message =>
      println(s"[CloudListener] Received message: ${msg.message}")
      messageGatherer ! msg
  }
}


class NetworkListener(messageGatherer: ActorRef) extends Actor {
  def receive: Receive = {
    case msg: Message =>
      println(s"[NetworkListener] Received message: ${msg.message}")
      messageGatherer ! msg
  }
}


class AppListener(messageGatherer: ActorRef) extends Actor {
  def receive: Receive = {
    case msg: Message =>
      println(s"[AppListener] Received message: ${msg.message}")
      messageGatherer ! msg
  }
}