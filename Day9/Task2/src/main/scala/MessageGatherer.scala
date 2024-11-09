
import akka.actor.Actor
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class MessageGatherer(producer: KafkaProducer[String, String]) extends Actor {
  def receive: Receive = {
    case message: String =>
      val record = new ProducerRecord[String, String]("consolidated-messages", message)
      producer.send(record)
      println(s"MessageGatherer sent message to consolidated-messages: $message")
  }
}
