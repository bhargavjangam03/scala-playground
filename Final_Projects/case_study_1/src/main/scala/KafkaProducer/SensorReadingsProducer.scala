package KafkaProducer

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scalapb.GeneratedMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import io.circe.syntax._ // For `.asJson` extension
import io.circe.generic.auto._
import models.SensorReading
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object SensorReadingsProducer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("KafkaSensorReadingProducer")

    // Kafka configuration
    val bootstrapServers = "localhost:9092"
    val topic = "sensor-readings"

    // Producer settings
    val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

    // Random generator
    val random = new Random()


    // Function to create a SensorReading message
    def createSensorReading(sensorId: Int, timestamp: Long, temperature: Float, humidity: Float): SensorReading = {
      SensorReading(sensorId = sensorId, timestamp = timestamp, temperature = temperature, humidity = humidity)
    }

    // Function to generate a Kafka producer record
    def buildRecord(): ProducerRecord[String, String] = {
      val sensorId = random.nextInt(100) + 1// Random sensor ID
      val timestamp = System.currentTimeMillis()         // Current timestamp
      val temperature = -50 + random.nextFloat() * 200   // Temperature between -50 and 150
      val humidity = random.nextFloat() * 100            // Humidity between 0 and 100

      val sensorReading = createSensorReading(sensorId, timestamp, temperature, humidity)
      val sensorReadingJson = sensorReading.asJson.noSpaces
      println(s"Generated Record: $sensorReading") // Debugging

      new ProducerRecord[String, String](topic,sensorReadingJson)
    }

    // Generate and stream sensor readings to Kafka
    val sensorReadings = Source.tick(0.seconds, 100.milliseconds, ())
      .map { _ => buildRecord() }

    // Run the Kafka producer
    sensorReadings
      .runWith(Producer.plainSink(producerSettings))
      .onComplete { result =>
        println(s"Kafka Producer completed with result: $result")
        system.terminate()
      }
  }
}
