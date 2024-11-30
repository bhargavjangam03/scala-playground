package Exercise5

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import scala.util.Random
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object KafkaOrderProducer {
  def main(args: Array[String]): Unit = {

    val kafkaTopic = "orders"

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    val random = new Random()

    // Define a list of users
    val users = (1 to 100).map(i => s"user$i")

    try {
      // Infinite loop to simulate order production
      while (true) {
        // Generate a random orderId and userId
        val orderId = s"order${Random.nextInt(1000)}"  // Random orderId like "order1", "order2"
        val userId = users(Random.nextInt(users.length))  // Random userId from the users list
        val amount = (Random.nextDouble() * 1000).round  // Random amount between 0 and 1000

        // Create a JSON structure for the order message
        val orderJson = ("userId" -> userId) ~
          ("orderId" -> orderId) ~
          ("orderAmount" -> amount)

        // Convert the JSON to a string
        val orderString = compact(render(orderJson))

        // Send the order to Kafka
        val record = new ProducerRecord[String, String](kafkaTopic, userId, orderString)
        producer.send(record)

        println(s"Produced order: $orderString")

        Thread.sleep(1000)
      }
    } finally {

      producer.close()
    }
  }
}

