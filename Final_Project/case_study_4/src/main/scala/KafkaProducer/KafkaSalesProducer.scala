package KafkaProducer

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import scalapb.GeneratedMessage
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scala.concurrent.duration._
import protobuf.SalesReport.SalesReport

import scala.concurrent.ExecutionContext.Implicits.global

object KafkaSalesProducer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("KafkaProtobufProducer")

    // Kafka configuration
    val bootstrapServers = "localhost:9092"
    val topic = "sales-topic"

    // Producer settings
    val producerSettings = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
      .withBootstrapServers(bootstrapServers)

    // Serialize Protobuf to byte array
    def serializeProtobuf[T <: GeneratedMessage](message: T): Array[Byte] = message.toByteArray

    // Function to create SalesReport messages
    def createSalesReport(store: String, dept: String, date: String, weeklySales: Double, isHoliday: Boolean): SalesReport =
      SalesReport(store = store, dept = dept, date = date, weeklySales = weeklySales, isHoliday = isHoliday)

    val tickSource = Source.tick(0.seconds, 10.seconds, ())
      .zipWithIndex // Adds an index to the ticks to use as the record number

    val salesRecords = tickSource.map { case (_, i) =>
      val salesReport = createSalesReport(
        store = i.toString,
        dept = (i % 5 + 1).toString,
        date = s"2024-12-${10 + i}",
        weeklySales = 10000 + i * 500,
        isHoliday = i % 2 == 0
      )
      new ProducerRecord[String, Array[Byte]](topic, salesReport.store, serializeProtobuf(salesReport))
    }


    // Stream records to Kafka
    salesRecords
      .runWith(Producer.plainSink(producerSettings))
      .onComplete { result =>
        println(s"Kafka Producer completed with result: $result")
        system.terminate()
      }
  }
}
