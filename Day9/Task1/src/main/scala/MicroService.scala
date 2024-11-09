import JsonFormats._
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future


// Main Application
object MicroserviceApp extends App {
  implicit val system: ActorSystem = ActorSystem("MicroserviceSystem")
  implicit val executionContext = system.dispatcher


  val producer = KafkaProducerFactory.createProducer()
  val networkProcessor = system.actorOf(Props(new NetworkMessageProcessor(producer)), "networkProcessor")
  val cloudProcessor = system.actorOf(Props(new CloudMessageProcessor(producer)), "cloudProcessor")
  val appProcessor = system.actorOf(Props(new AppMessageProcessor(producer)), "appProcessor")
  val messageHandler = system.actorOf(Props(new MessageHandler(networkProcessor, cloudProcessor, appProcessor)), "messageHandler")


  val route = post {
    path("process-Message") {
      entity(as[Message]) { message =>
        messageHandler ! message
        complete(StatusCodes.OK, s"Message processed: $message")
      }
    }
  }

  // Start HTTP server
  Http().newServerAt("0.0.0.0", 8080).bind(route)
  println("Server online at http://0.0.0.0:8080/")
}