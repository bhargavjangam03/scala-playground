package KafkaConsumer

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import models.JsonFormats.SensorReadingReads
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.{Json, OFormat}
import models.SensorReading
import org.apache.spark.sql.SparkSession
import spray.json._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.protobuf.functions.from_protobuf
import utils.GcsOperations.{storeSensorDataInGCP,processBatchDataAndStore}

import java.time.LocalDateTime

object SensorDataConsumer {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Sensor Data Consumer")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/resources/spark-gcs-key.json")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Kafka configuration
    val topic = "sensor-readings"
    val kafkaBootstrapServers = "localhost:9092"

    // Read messages from kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()
    // Deserialize the JSON payload into SensorReading
    val sensorReadingsDF = kafkaDF.selectExpr("CAST(value AS STRING)").as[String]
      .map(json => {
        // Parse the JSON string into a SensorReading object
        val reading = Json.parse(json).as[SensorReading]
        (reading.sensorId, reading.timestamp, reading.temperature, reading.humidity)
      }).toDF("sensorId", "timestamp", "temperature", "humidity")

    // Validate the data
    val validatedDF = sensorReadingsDF.filter(
      col("sensorId").isNotNull &&
        col("timestamp").isNotNull &&
        col("temperature").isNotNull &&
        col("humidity").isNotNull &&
        col("sensorId").cast("int").isNotNull &&
        col("timestamp").cast("int").isNotNull &&
        col("temperature").cast("float").isNotNull &&
        col("humidity").cast("float").isNotNull &&
        col("temperature").between(-50, 150) &&
        col("humidity").between(0, 100)
    )

    // Convert the validated DataFrame into Protobuf format
    val protobufSchema = "src/main/scala/protobuf/SensorReading"  // Protobuf schema path
    val protobufDF = validatedDF.select(
      from_protobuf(col("sensorId"), protobufSchema).alias("sensorId"),
      from_protobuf(col("timestamp"), protobufSchema).alias("timestamp"),
      from_protobuf(col("temperature"), protobufSchema).alias("temperature"),
      from_protobuf(col("humidity"), protobufSchema).alias("humidity")
    )

    // Process this dataframe
    val query = protobufDF.writeStream
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .foreachBatch { (batchDF: Dataset[Row], _: Long) => processSensorData(spark, batchDF) }
      .start()

    query.awaitTermination()
    spark.stop()
  }

  private def processSensorData(spark: SparkSession, batchDF: DataFrame): Unit = {
    batchDF.cache()
    val currentProcessingTime = LocalDateTime.now()
    storeSensorDataInGCP(batchDF, currentProcessingTime)
    processBatchDataAndStore(spark, batchDF, currentProcessingTime)
  }

}
