package services

import models.request.KafkaMessage
import play.api.libs.json._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import javax.inject._

@Singleton
class KafkaProducerService @Inject()() {

  private val props = new Properties()
  props.put("bootstrap.servers", "10.128.0.2:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  def sendToKafka(kafkaRequest: KafkaMessage): Unit = {
    // Serialize the Visitor object to JSON
    val jsonMessage: String = Json.stringify(Json.toJson(kafkaRequest))

    // Send the message to Kafka
    val record = new ProducerRecord[String, String]("waverock-visitor", "key", jsonMessage)
    producer.send(record)
  }
}
