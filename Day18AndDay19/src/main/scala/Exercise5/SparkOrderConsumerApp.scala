package Exercise5

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.streaming.Trigger

object SparkOrderConsumerApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Kafka Consumer with Windowing")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/scala/spark-gcs-key.json")
      .master("local[*]")
      .getOrCreate()

    // Set log level to WARN to reduce logging verbosity
    spark.sparkContext.setLogLevel("WARN")

    // Define Kafka parameters
    val kafkaBootstrapServers = "localhost:9092"  // Update with your Kafka broker address
    val kafkaTopic = "orders"  // Kafka topic to consume data from

    // Read the user details CSV file from GCS
    val userDetailsPath = "gs://bhargav-assignments/Day18And19Task/Exercise_5/input/user_details.csv"
    val userDetailsDF = spark.read.option("header", "true").csv(userDetailsPath)

    println("Original DataFrame:")
    userDetailsDF.show(10)

    // Broadcast the user details DataFrame for efficient joins
    val broadCastedUserDetailsDF = broadcast(userDetailsDF)

    val rawKafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", kafkaTopic)
      .load()

    // Deserialize Kafka message and parse as JSON
    val schema = new StructType()
      .add("orderId", StringType)
      .add("userId", StringType)
      .add("orderAmount", IntegerType)


    val ordersDF = rawKafkaDF.selectExpr("CAST(value AS STRING) AS order_data")
      .select(from_json(col("order_data"), schema).as("order"))
      .select("order.*")



    // Perform a broadcast join between the orders and user details
    val enrichedOrdersDF = ordersDF.join(
      broadCastedUserDetailsDF,
      ordersDF("userId") === broadCastedUserDetailsDF("userId"),
      "left"
    ).select(
      ordersDF("orderId"),
      ordersDF("userId"),
      ordersDF("orderAmount"),
      broadCastedUserDetailsDF("userName"),
      broadCastedUserDetailsDF("userEmail")
    )

    // Write the enriched data back to GCS in JSON format
    val outputPath = "gs://bhargav-assignments/Day18AndDay19Task/Exercise_5/output/enriched_orders/"

    val query = enrichedOrdersDF.writeStream
      .outputMode("append")
      .format("json")
      .option("checkpointLocation", "gs://bhargav-assignments/Day18And19Task/Exercise_5/output/checkpoint/")
      .option("path", outputPath)
      .trigger(Trigger.ProcessingTime("20 seconds"))
      .start()

    query.awaitTermination()
  }
}
