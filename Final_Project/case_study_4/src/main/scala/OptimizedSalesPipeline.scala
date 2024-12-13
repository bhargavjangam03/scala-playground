import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window



object OptimizedSalesPipeline {

  def main(args: Array[String]): Unit = {

    // Initialize Spark Session
    val spark = SparkSession.builder()
      .appName("Optimized Sales Data Pipeline")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/resources/spark-gcs-key.json")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Initial Data Loading and Dataframes
    val trainDF = spark.read.option("header", "true").option("inferSchema", "true").csv("src/main/resources/train.csv")
    val featuresDF = spark.read.option("header", "true").option("inferSchema", "true").csv("src/main/resources/features.csv")
    val storesDF = spark.read.option("header", "true").option("inferSchema", "true").csv("src/main/resources/stores.csv")

    // Data validation
    val validatedTrainDF = trainDF
      .filter(col("Weekly_Sales").isNotNull && col("Weekly_Sales") >= 0)
      .filter(col("Store").isNotNull && col("Dept").isNotNull)

    val broadCastedStoresDF = broadcast(storesDF.cache())
    val broadCastedFeaturesDF = broadcast(featuresDF.cache())

    // Enrich the training data
    val enrichedDF = validatedTrainDF
      .join(broadCastedStoresDF, Seq("Store"), "inner")
      .join(broadCastedFeaturesDF, Seq("Store"), "inner")

    // Step 4: Compute Initial Store and Department Metrics

    // Store Metrics
    val storeMetricsDF = enrichedDF.groupBy("Store")
      .agg(
        sum("Weekly_Sales").alias("Total_Sales"),
        count("Weekly_Sales").alias("Number_of_Sales"),
        avg("Weekly_Sales").alias("Avg_Weekly_Sales")
      )
      .cache()

    // First Store JSON
    storeMetricsDF.write.mode("overwrite").json("gs://bhargav-assignments/final_project/case_study_4/store_metrics.json")

    // Top 10 Stores based on total sales
    val topStoresDF = storeMetricsDF
      .orderBy(desc("Total_Sales"))
      .limit(10)

    // Second Store JSON (Top 10)
    topStoresDF.write.mode("overwrite").json("gs://bhargav-assignments/final_project/case_study_4/top_performing_stores.json")

    // Department Metrics
    val deptMetricsDF = enrichedDF.groupBy("Store", "Dept", "Date")
      .agg(
        sum("Weekly_Sales").alias("Total_Sales"),
        sum(when($"Is_Holiday" === true, 1).otherwise(0)).alias("Holiday_Sales"),
        sum(when($"Is_Holiday" === false, 1).otherwise(0)).alias("Non_Holiday_Sales")
      )
      .cache()

    // First Department JSON (Total sales, holiday, and non-holiday sales)
    deptMetricsDF.write.mode("overwrite").json("gs://bhargav-assignments/final_project/case_study_4/dept_metrics.json")

    // Department trend (weekly sales, previous sales, and trend)
    val deptTrendDF = enrichedDF.groupBy("Store", "Dept", "Date")
      .agg(
        sum("Weekly_Sales").alias("Weekly_Sales"),
        lag("Weekly_Sales", 1).over(Window.partitionBy("Store", "Dept").orderBy("Date")).alias("Previous_Weekly_Sales")
      )
      .withColumn("Trend",
        when($"Weekly_Sales" > $"Previous_Weekly_Sales", concat(lit("Increase by "), $"Weekly_Sales" - $"Previous_Weekly_Sales"))
          .when($"Weekly_Sales" < $"Previous_Weekly_Sales", concat(lit("Decrease by "), $"Previous_Weekly_Sales" - $"Weekly_Sales"))
          .otherwise(lit("No Change"))
      )
      .cache()

    // Second Department JSON (Weekly sales, previous sales, and trend)
    deptTrendDF.write.mode("overwrite").json("gs://bhargav-assignments/final_project/case_study_4/dept_trend_metrics.json")

    // Step 5: Real-Time Data Processing (Streaming)

    val kafkaBootstrapServers = "localhost:9092"
    val topic = "sales-topic"
    val enrichedParquetDir = "gs://bhargav-assignments/final_project/case_study_4/enriched_data"
    val metricsOutputDir = "gs://bhargav-assignments/final_project/case_study_4"

    // Read from Kafka and process in real-time
    // Read messages from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    // Extract Protobuf binary data and deserialize to DataFrame
    val salesStreamDF = kafkaDF
      .selectExpr("CAST(value AS BINARY) as value") // Extract binary Protobuf data
      .select(from_protobuf($"value", messageType, descriptorFile).alias("salesRecord")) // Deserialize Protobuf
      .select("salesRecord.*") // Flatten the struct for individual fields
      .na.fill(Map(
        "is_holiday" -> false,      // Default boolean value
        "weekly_sales" -> 0.0f      // Default float value
      ))

    // Real-time processing of data
    val streamingQuery = salesStreamDF
      .writeStream
      .foreachBatch { (batchDF: DataFrame, _: Long) =>
        // Enrich incoming data
        val enrichedStreamDF = batchDF
          .join(broadCastedFeaturesDF, Seq("Store"), "inner")
          .join(broadCastedStoresDF, Seq("Store"), "inner")

        // Save enriched data
        enrichedStreamDF.write
          .mode("append")
          .partitionBy("Store", "Date")
          .parquet(enrichedParquetDir)

        // Update store-level metrics
        val updatedStoreMetricsDF = enrichedStreamDF.groupBy("Store")
          .agg(
            sum("Weekly_Sales").alias("Batch_Total_Sales"),
            count("Weekly_Sales").alias("Batch_Sales_Count"),
            avg("Weekly_Sales").alias("Avg_Weekly_Sales")
          )
          .join(storeMetricsDF, Seq("Store"), "left")
          .withColumn("Total_Sales", col("Batch_Total_Sales") + col("Total_Sales"))
          .withColumn("Number_of_Sales", col("Batch_Sales_Count") + col("Number_of_Sales"))
          .withColumn("Avg_Weekly_Sales", (col("Batch_Total_Sales") + col("Total_Sales")) / (col("Number_of_Sales") + col("Batch_Sales_Count")))
          .select("Store", "Total_Sales", "Number_of_Sales", "Avg_Weekly_Sales")

        // Save updated store metrics
        updatedStoreMetricsDF.write.mode("overwrite").json(s"$metricsOutputDir/store_metrics.json")
        updatedStoreMetricsDF.write.mode("overwrite").parquet(s"$metricsOutputDir/store_metrics.parquet")

        // Get Top 10 Stores by Total Sales
        val topStoresDF = updatedStoreMetricsDF
          .orderBy(desc("Total_Sales"))
          .limit(10)

        // Save the top-performing stores
        topStoresDF.write.mode("overwrite").json(s"$metricsOutputDir/top_performing_stores.json")
        topStoresDF.write.mode("overwrite").parquet(s"$metricsOutputDir/top_performing_stores.parquet")

        // Update department-level metrics
        val updatedDeptMetricsDF = enrichedStreamDF.groupBy("Store", "Dept", "Date")
          .agg(
            sum("Weekly_Sales").alias("Weekly_Sales"),
            sum(when($"Is_Holiday" === true, 1).otherwise(0)).alias("Holiday_Sales"),
            sum(when($"Is_Holiday" === false, 1).otherwise(0)).alias("Non_Holiday_Sales")
          )
          .cache()

        updatedDeptMetricsDF.write.mode("overwrite").json(s"$metricsOutputDir/dept_metrics.json")
        updatedDeptMetricsDF.write.mode("overwrite").parquet(s"$metricsOutputDir/dept_metrics.parquet")

        // Department trend (weekly sales, previous sales, and trend)
        val updatedDeptTrendDF = enrichedStreamDF.groupBy("Store", "Dept", "Date")
          .agg(
            sum("Weekly_Sales").alias("Weekly_Sales"),
            lag("Weekly_Sales", 1).over(Window.partitionBy("Store", "Dept").orderBy("Date")).alias("Previous_Weekly_Sales")
          )
          .withColumn("Trend",
            when($"Weekly_Sales" > $"Previous_Weekly_Sales", concat(lit("Increase by "), $"Weekly_Sales" - $"Previous_Weekly_Sales"))
              .when($"Weekly_Sales" < $"Previous_Weekly_Sales", concat(lit("Decrease by "), $"Previous_Weekly_Sales" - $"Weekly_Sales"))
              .otherwise(lit("No Change"))
          )
          .cache()

        updatedDeptTrendDF.write.mode("overwrite").json(s"$metricsOutputDir/dept_trend_metrics.json")
        updatedDeptTrendDF.write.mode("overwrite").parquet(s"$metricsOutputDir/dept_trend_metrics.parquet")
      }
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("1 minute"))
      .start()

    streamingQuery.awaitTermination()
  }
}
