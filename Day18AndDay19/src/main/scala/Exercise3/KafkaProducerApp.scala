package Exercise3

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import scala.util.Random

object KafkaProducerApp {
  def main(args: Array[String]): Unit = {
    val topic = "transactions"

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    val random = new Random()

    val users = (1 to 10).map(i => s"user_$i")

    try {
      while (true) {
        val transactionId = Random.alphanumeric.take(10).mkString
        val userId = users(random.nextInt(users.length))
        val amount = random.nextInt(1000) + 1
        val message =
          s"""
             |{
             |  "transactionId": "$transactionId",
             |  "userId": "$userId",
             |  "amount": $amount
             |}
             |""".stripMargin

        val record = new ProducerRecord[String, String](topic, userId, message)
        producer.send(record)

        println(s"Sent: $message")
        Thread.sleep(1000)
      }
    } finally {
      producer.close()
    }
  }
}

