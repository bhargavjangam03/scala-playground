// app/services/KafkaProducerService.scala

package services

import org.apache.kafka.common.serialization.StringSerializer
import play.api.{Configuration, Logging}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future, Promise}

@Singleton
class KafkaProducerService @Inject()(config: Configuration)(implicit ec: ExecutionContext) extends Logging {

  private val bootstrapServers = config.get[String]("kafka.bootstrap.servers")
  private val topic = config.get[String]("kafka.topic")

  // Kafka producer properties
  private val producerProperties = {
    val props = new java.util.Properties()
    props.put("bootstrap.servers", bootstrapServers)
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)
    props.put("acks", config.get[String]("kafka.acks"))
    props.put("retries", config.get[Int]("kafka.retries").toString)
    props
  }

  // Create Kafka producer
  private val producer = new KafkaProducer[String, String](producerProperties)

  /**
   * Sends a message to Kafka with guest information.
   * @param guestName The name of the guest.
   * @param guestEmail The email of the guest.
   * @return Future indicating the success or failure of the send operation.
   */
  def sendGuestBookingMessage(guestName: String, guestEmail: String): Future[RecordMetadata] = {
    val message = s"""{"guestName": "$guestName", "guestEmail": "$guestEmail"}"""
    val record = new ProducerRecord[String, String](topic, guestEmail, message) // Using guestEmail as the key

    val promise = Promise[RecordMetadata]()
    producer.send(record, (metadata: RecordMetadata, exception: Exception) => {
      if (exception == null) {
        promise.success(metadata)
        logger.info(s"Sent booking message for $guestName to Kafka topic $topic")
      } else {
        promise.failure(exception)
        logger.error(s"Failed to send booking message for $guestName to Kafka", exception)
      }
    })
    promise.future
  }

  // Close the producer when application stops
  sys.addShutdownHook {
    producer.close()
  }
}
