import akka.actor.{ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import models.GuestInfo
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import actors.MailActor
import models.JsonFormats.guestFormat
import spray.json._
object WifiServiceApp extends App {
  implicit val system: ActorSystem = ActorSystem("WifiServiceConsumerSystem")

  private val mailListener = system.actorOf(Props(new MailActor), "mailListener")

  private val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("wifi-listeners-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  Consumer.plainSource(consumerSettings, akka.kafka.Subscriptions.topics("Taj-Guest-List"))
    .map(record => record.value().parseJson.convertTo[GuestInfo])
    .runWith(Sink.foreach(message => mailListener ! message))


}