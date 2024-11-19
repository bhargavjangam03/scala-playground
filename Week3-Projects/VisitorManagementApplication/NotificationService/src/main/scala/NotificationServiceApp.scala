import org.apache.kafka.common.serialization.StringDeserializer
import akka.actor.{ActorSystem, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig


object NotificationServiceApp extends App {

  val system = ActorSystem("VisitorNotificationSystem")
  implicit val materializer: Materializer = ActorMaterializer()(system)

  val itSupportProcessor = system.actorOf(Props[ITSupportProcessor], "itSupportProcessor")
  val hostProcessor = system.actorOf(Props[HostProcessor], "hostProcessor")
  val securityProcessor = system.actorOf(Props[SecurityProcessor], "securityProcessor")

  val notificationHandler = system.actorOf(Props(new NotificationHandler(itSupportProcessor, hostProcessor, securityProcessor)), "notificationHandler")

  // Kafka consumer settings
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("visitor-notification-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  Consumer.plainSource(consumerSettings, Subscriptions.topics("waverock-visitor"))
    .runWith(Sink.foreach(message => notificationHandler ! message))

  println("Listeners are active and consuming messages...")
}

