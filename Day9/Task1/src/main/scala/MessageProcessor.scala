import akka.actor.{Actor, ActorRef}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
// Actor definitions
class NetworkMessageProcessor(producer: KafkaProducer[String, String]) extends Actor {
  override def receive: Receive = {
    case message: Message =>
      val record = new ProducerRecord[String, String]("network-message", message.messageKey, message.message)
      producer.send(record)
      println(s"NetworkMessage processed: ${message.message}")
  }
}

class CloudMessageProcessor(producer: KafkaProducer[String, String]) extends Actor {
  override def receive: Receive = {
    case message: Message =>
      val record = new ProducerRecord[String, String]("cloud-message", message.messageKey, message.message)
      producer.send(record)
      println(s"CloudMessage processed: ${message.message}")
  }
}

class AppMessageProcessor(producer: KafkaProducer[String, String]) extends Actor {
  override def receive: Receive = {
    case message: Message =>
      val record = new ProducerRecord[String, String]("app-message", message.messageKey, message.message)
      producer.send(record)
      println(s"AppMessage processed: ${message.message}")
  }
}

// MessageHandler to route messages to the correct processor
class MessageHandler(networkProcessor: ActorRef, cloudProcessor: ActorRef, appProcessor: ActorRef) extends Actor {
  override def receive: Receive = {
    case message: Message =>
      message.messageType match {
        case "NetworkMessage" => networkProcessor ! message
        case "CloudMessage" => cloudProcessor ! message
        case "AppMessage" => appProcessor ! message
        case _ => println(s"Unknown message type: ${message.messageType}")
      }
  }
}