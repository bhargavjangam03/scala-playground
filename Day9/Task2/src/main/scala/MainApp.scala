// MainApp.scala
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.kafka.ConsumerSettings
import org.apache.kafka.common.serialization.StringDeserializer
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import spray.json._
import JsonFormats._

object MainApp extends App {


  implicit val system: ActorSystem = ActorSystem("KafkaListenersSystem")

  val producer = KafkaProducerFactory.createProducer()
  val messageGatherer = system.actorOf(Props(new MessageGatherer(producer)), "messageGatherer")

  private val cloudListener = system.actorOf(Props(new CloudListener(messageGatherer)), "cloudListener")
  private val networkListener = system.actorOf(Props(new NetworkListener(messageGatherer)), "networkListener")
  private val appListener = system.actorOf(Props(new AppListener(messageGatherer)), "appListener")


  private val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("message-listeners-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")


  Consumer.plainSource(consumerSettings, akka.kafka.Subscriptions.topics("cloud-message"))
    .map(record => record.value().parseJson.convertTo[Message])
    .runWith(Sink.foreach(message => cloudListener ! message))


  Consumer.plainSource(consumerSettings, akka.kafka.Subscriptions.topics("network-message"))
    .map(record => record.value().parseJson.convertTo[Message])
    .runWith(Sink.foreach(message => networkListener ! message))


  Consumer.plainSource(consumerSettings, akka.kafka.Subscriptions.topics("app-message"))
    .map(record => record.value().parseJson.convertTo[Message])
    .runWith(Sink.foreach(message => appListener ! message))

  println("Listeners are active and consuming messages...")
}
