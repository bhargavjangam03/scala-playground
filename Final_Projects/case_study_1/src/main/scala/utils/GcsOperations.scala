package utils

import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.protobuf.functions._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.functions._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.spark.sql.protobuf.functions.from_protobuf

object GcsOperations {

  val sensorDataMessageType: String = "protobuf.SensorReading"
  val aggregatedMessageType: String = "protobuf.AggregatedSensorReading"
  val rawDataProtoDescriptorPath = "src/main/scala/protobuf/descriptor/SensorReading.desc"
  val aggregatedDataProtoDescriptorPath = "src/main/scala/protobuf/descriptor/AggregatedSensorReading.desc"
  val gsEventTimeBasedRawDataPath = "gs://bhargav-assignments/final_project/partitioned_by_time/case_study_1/raw/sensor-data/"
  val gsProcessingTimeBasedRawDataParentPath = "gs://bhargav-assignments/final_project/case_study_1/raw/sensor-data/"
  val gsAggregationDataParentPath = "gs://bhargav-assignments/final_project/case_study_1/aggregated/protobuf"
  val gsAggregationJsonDataParentPath = "gs://bhargav-assignments/final_project/case_study_1/aggregated/json"


  def processBatchDataAndStore(spark: SparkSession, batchDF: DataFrame, currentProcessingTime: LocalDateTime): Unit = {
    import spark.implicits._

    val hadoopConf = spark.sparkContext.hadoopConfiguration
    val fs = FileSystem.get(hadoopConf)

    def readExistingAggregatedData(fp: String): DataFrame = {
      spark.read.parquet(fp)
        .select(from_protobuf($"value", aggregatedMessageType, aggregatedDataProtoDescriptorPath).alias("aggregatedData"))
        .select(
          "aggregatedData.sensorId",
          "aggregatedData.averageTemperature",
          "aggregatedData.averageHumidity",
          "aggregatedData.minimumTemperature",
          "aggregatedData.maximumTemperature",
          "aggregatedData.minimumHumidity",
          "aggregatedData.maximumHumidity",
          "aggregatedData.noOfRecords"
        )
    }

    try {
      // Load existing aggregated data
      val previousProcessingTime = currentProcessingTime.minusHours(1)
      val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")
      val currentHourAggregatedFolder = s"${gsAggregationDataParentPath}/${currentProcessingTime.format(formatter)}"
      val previousHourAggregatedFolder = s"${gsAggregationDataParentPath}/${previousProcessingTime.format(formatter)}"

      val existingAggregatedData: DataFrame = {
        if (fs.exists(new Path(currentHourAggregatedFolder))) readExistingAggregatedData(currentHourAggregatedFolder)
        else if (fs.exists(new Path(previousHourAggregatedFolder))) readExistingAggregatedData(previousHourAggregatedFolder)
        else null // for the first time aggregation
      }

      if (existingAggregatedData != null) existingAggregatedData.cache()  // repeated usage

      // Perform Incremental Aggregation
      val updatedAggregatedDF = computeAggregatedData(existingAggregatedData, batchDF).cache()  // repeated usage
      // updatedAggregatedDF.show()

      // Store these metrics inside the GCP
      storeAggregatedMetricsInGCP(updatedAggregatedDF, currentProcessingTime)
    }
    finally {
      fs.close()
    }
  }

  def computeAggregatedData(existingAggregatedData: DataFrame, batchDF: DataFrame): DataFrame = {
    val batchDataAggregatedDF = getBatchAggregatedDF(batchDF)

    val computedAggData = existingAggregatedData match {
      case null => batchDataAggregatedDF
      case _ =>
        existingAggregatedData.join(batchDataAggregatedDF, Seq("sensorId"), "fullOuterJoin")
          .select(
            coalesce(batchDataAggregatedDF("sensorId"), existingAggregatedData("storeId")).alias("storeId"),
            (
              (
                (coalesce(batchDataAggregatedDF("averageTemperature"), lit(0.0f)) * coalesce(batchDataAggregatedDF("noOfRecords"), lit(0)))
                  +(coalesce(existingAggregatedData("averageTemperature"), lit(0.0f)) * coalesce(existingAggregatedData("noOfRecords"), lit(0)))
                ) / (
                coalesce(batchDataAggregatedDF("noOfRecords"), lit(0)) + coalesce(existingAggregatedData("noOfRecords"), lit(0))
                )
              ).cast("float")
              .alias("averageTemperature"),
            (
              (
                (coalesce(batchDataAggregatedDF("averageHumidity"), lit(0.0f)) * coalesce(batchDataAggregatedDF("noOfRecords"), lit(0)))
                  +(coalesce(existingAggregatedData("averageHumidity"), lit(0.0f)) * coalesce(existingAggregatedData("noOfRecords"), lit(0)))
                ) / (
                coalesce(batchDataAggregatedDF("noOfRecords"), lit(0)) + coalesce(existingAggregatedData("noOfRecords"), lit(0))
                )
              ).cast("float")
              .alias("averageHumidity"),
            least(batchDataAggregatedDF("minimumTemperature"), existingAggregatedData("minimumTemperature")).alias("minimumTemperature"),
            greatest(batchDataAggregatedDF("maximumTemperature"), existingAggregatedData("maximumTemperature")).alias("maximumTemperature"),
            least(batchDataAggregatedDF("minimumHumidity"), existingAggregatedData("minimumHumidity")).alias("minimumHumidity"),
            greatest(batchDataAggregatedDF("maximumHumidity"), existingAggregatedData("maximumHumidity")).alias("maximumHumidity"),
            (coalesce(batchDataAggregatedDF("noOfRecords"), lit(0))
              + coalesce(existingAggregatedData("noOfRecords"), lit(0))).alias("noOfRecords")
          )
    }
    computedAggData
  }

  def getBatchAggregatedDF(batchDF: DataFrame): DataFrame = {
    batchDF
      .groupBy("sensorId")
      .agg(
        avg(col("temperature")).cast("float").alias("averageTemperature"),
        avg(col("humidity")).cast("float").alias("averageHumidity"),
        min(col("temperature")).alias("minimumTemperature"),
        max(col("temperature")).alias("maximumTemperature"),
        min(col("humidity")).alias("minimumHumidity"),
        max(col("humidity")).alias("maximumHumidity"),
        count(lit(1)).alias("noOfRecords")
      )
  }

  def storeAggregatedMetricsInGCP(aggregatedMetricsDF: DataFrame, currentProcessingTime: LocalDateTime): Unit = {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")

    val aggregatedProtoFP = s"${gsAggregationDataParentPath}/${currentProcessingTime.format(formatter)}"
    val aggregatedJsonFP = s"${gsAggregationJsonDataParentPath}/${currentProcessingTime.format(formatter)}"

    // Store the Proto binary format
    aggregatedMetricsDF
      .withColumn("value", to_protobuf(struct(aggregatedMetricsDF.columns.map(col): _*), aggregatedMessageType, aggregatedDataProtoDescriptorPath))
      .select(col("value"))
      .write
      .mode(SaveMode.Overwrite)
      .parquet(aggregatedProtoFP)

    // Store the JSON format
    aggregatedMetricsDF.write.mode(SaveMode.Overwrite).json(aggregatedJsonFP)


  }

  def storeSensorDataInGCP(sensorBatchData: DataFrame, currentProcessingTime: LocalDateTime): Unit = {
    // Store the raw data by serialising it to protobuf
    storeRawDataPartitionedByTime(sensorBatchData)

    // Doing this for faster fetching in the next stages
    storeRawDataByProcessingTime(sensorBatchData, currentProcessingTime)
  }

  def storeRawDataPartitionedByTime(sensorBatchData: DataFrame): Unit = {
    val partitionedDF = sensorBatchData
      .withColumn("value", to_protobuf(struct(sensorBatchData.columns.map(col): _*), sensorDataMessageType, rawDataProtoDescriptorPath))
      .withColumn("year", from_unixtime(col("timestamp") / 1000, "yyyy")) // Convert milliseconds to seconds
      .withColumn("month", from_unixtime(col("timestamp") / 1000, "MM"))
      .withColumn("day", from_unixtime(col("timestamp") / 1000, "dd"))
      .withColumn("hour", from_unixtime(col("timestamp") / 1000, "HH"))

    partitionedDF
      .select(col("value"), col("year"), col("month"), col("day"), col("hour"))
      .write
      .mode(SaveMode.Append)
      .partitionBy("year", "month", "day", "hour")
      .format("parquet")
      .save(gsEventTimeBasedRawDataPath)
  }

  def storeRawDataByProcessingTime(sensorBatchData: DataFrame, currentProcessingTime: LocalDateTime): Unit = {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")
    val fp = s"${gsProcessingTimeBasedRawDataParentPath}/${currentProcessingTime.format(formatter)}"

    sensorBatchData
      .withColumn("value", to_protobuf(struct(sensorBatchData.columns.map(col): _*), sensorDataMessageType, rawDataProtoDescriptorPath))
      .select(col("value"))
      .write
      .mode(SaveMode.Append)
      .format("parquet")
      .save(fp)
  }
}
