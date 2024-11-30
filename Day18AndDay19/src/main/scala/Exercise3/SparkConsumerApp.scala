package Exercise3

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object SparkConsumerApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Kafka Consumer with Windowing")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // Define the schema for the incoming JSON messages
    val schema = new StructType()
      .add("transactionId", StringType)
      .add("userId", StringType)
      .add("amount", IntegerType)

    // Read data from Kafka
    val kafkaStream = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "transactions")
      .option("startingOffsets", "latest")
      .load()

    // Parse the value column as JSON and extract the required fields
    val transactions = kafkaStream.selectExpr("CAST(value AS STRING) as json")
      .select(from_json(col("json"), schema).as("data"))
      .select("data.transactionId", "data.amount", "data.userId")

    val transactionsWithTimestamp = transactions
      .withColumn("timestamp", current_timestamp())

    // Apply a 10-second tumbling window to calculate the total amount
    val windowedAggregates = transactionsWithTimestamp
      .withWatermark("timestamp", "10 seconds")
      .groupBy(window(col("timestamp"), "10 seconds"))
      .agg(sum("amount").alias("totalAmount"))


    // Write the output to the console
    val query = windowedAggregates.writeStream
      .outputMode("update")
      .format("console")
      .option("truncate", "false")
      .start()

    query.awaitTermination()
  }
}

