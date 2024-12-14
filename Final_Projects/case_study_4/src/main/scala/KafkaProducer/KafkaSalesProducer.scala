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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

    // Generate sales data starting from the first Sunday of 2013
    val startDate = LocalDate.parse("2013-01-06", DateTimeFormatter.ISO_DATE) // First Sunday of 2013
    val maxStores = 45
    val salesRange = 10000 to 50000
    val random = new scala.util.Random()

    // Generate tick source every 10 seconds
    val tickSource = Source.tick(0.seconds, 1.seconds, ())
      .zipWithIndex // Adds an index to determine the date offset and other record variations

    // Map ticks to sales records
    val salesRecords = tickSource.map { case (_, i) =>
      val currentDate = startDate.plusWeeks(i.toLong) // Add weeks based on the index
      val store = (i % maxStores + 1).toString // Rotate store numbers from 1 to 45
      val dept = (i % 50 + 1).toString // Rotate department numbers from 1 to 5
      val salesValue = salesRange.start + random.nextInt(salesRange.end - salesRange.start) // Random sales value in range
      val isHoliday = random.nextBoolean() // Randomly assign holiday status

      // Create the sales report
      val salesReport = createSalesReport(
        store = store,
        dept = dept,
        date = currentDate.format(DateTimeFormatter.ISO_DATE),
        weeklySales = salesValue,
        isHoliday = isHoliday
      )

      // Create Kafka producer record
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
