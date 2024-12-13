import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import org.apache.spark.sql.protobuf.functions.from_protobuf


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

    val descriptorFile = "src/main/scala/protobuf/descriptor/SalesReport.desc"
    val messageType = "protobuf.SalesReport"
    val store_folder_path = "gs://bhargav-assignments/final_project/case_study_4/store_data"
    val department_folder_path = "gs://bhargav-assignments/final_project/case_study_4/department_data"
    val metricsOutputDir = "gs://bhargav-assignments/final_project/case_study_4"
    val enrichedParquetDir = "gs://bhargav-assignments/final_project/case_study_4/enriched_data"
    val kafkaBootstrapServers = "localhost:9092"
    val topic = "sales-topic"

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

    println("Initial Enriched DataFrame:")
    enrichedDF.show(5)
    // Save enriched data
    enrichedDF.write
      .mode("append")
      .partitionBy("Store", "Date")
      .parquet(enrichedParquetDir)

    // Step 4: Compute Initial Store and Department Metrics

    // Store Metrics
    val storeMetricsDF = enrichedDF.groupBy("Store")
      .agg(
        sum("Weekly_Sales").alias("Total_Sales"),
        count("Weekly_Sales").alias("Number_of_Sales"),
        avg("Weekly_Sales").alias("Avg_Weekly_Sales")
      )
      .cache()

    println("Initial Store Metrics DataFrame:")
    storeMetricsDF.show(5)

    // First Store JSON
    storeMetricsDF.write.mode("overwrite").json(s"$store_folder_path/store_metrics.json")

    // Top 10 Stores based on total sales
    val topStoresDF = storeMetricsDF
      .orderBy(desc("Total_Sales"))
      .limit(10)

    println("Initial Top Performing Stores DataFrame:")
    topStoresDF.show(5)
    // Second Store JSON (Top 10)
    topStoresDF.write.mode("overwrite").json(s"$store_folder_path/top_performing_stores.json")

    // Department Metrics
    val deptMetricsDF = enrichedDF.groupBy("Store", "Dept")
      .agg(
        sum("Weekly_Sales").alias("Total_Sales"),
        sum(when($"Is_Holiday" === true, 1).otherwise(0)).alias("Holiday_Sales"),
        sum(when($"Is_Holiday" === false, 1).otherwise(0)).alias("Non_Holiday_Sales")
      )
      .cache()

    println("Initial Department Metrics DataFrame:")
    deptMetricsDF.show(5)

    // First Department JSON (Total sales, holiday, and non-holiday sales)
    deptMetricsDF.write.mode("overwrite").json(s"$department_folder_path/dept_metrics.json")

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

    println("Initial Department Weekly-Trends DataFrame:")
    deptTrendDF.show(5)

    // Second Department JSON (Weekly sales, previous sales, and trend)
    deptTrendDF.write.mode("overwrite").json(s"$department_folder_path/dept_trend_metrics.json")

    // Step 5: Real-Time Data Processing (Streaming)

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
      .select(from_protobuf($"value", messageType, descriptorFile).alias("SalesReport")) // Deserialize Protobuf
      .select("SalesReport.*") // Flatten the struct for individual fields
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

        println("New Enriched DataFrame:")
        enrichedStreamDF.show(5)

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

        println("Updated Store Metrics Dataframe")
        updatedStoreMetricsDF.show(5)
        // Save updated store metrics
        updatedStoreMetricsDF.write.mode("overwrite").json(s"$store_folder_path/store_metrics.json")

        // Get Top 10 Stores by Total Sales
        val updatedTopStoresDF = updatedStoreMetricsDF
          .orderBy(desc("Total_Sales"))
          .limit(10)

        println("Updated Top Performing Stores DataFrame")
        updatedTopStoresDF.show(5)
        // Save the top-performing stores
        updatedTopStoresDF.write.mode("overwrite").json(s"$store_folder_path/top_performing_stores.json")

        // Update department-level metrics
        val updatedDeptMetricsDF = enrichedStreamDF.groupBy("Store", "Dept")
          .agg(
            sum("Weekly_Sales").alias("Weekly_Sales"),
            sum(when($"Is_Holiday" === true, 1).otherwise(0)).alias("Holiday_Sales"),
            sum(when($"Is_Holiday" === false, 1).otherwise(0)).alias("Non_Holiday_Sales")
          )
          .cache()

        println("Updated Department Metrics DataFrame:")
        updatedDeptMetricsDF.show(5)

        updatedDeptMetricsDF.write.mode("overwrite").json(s"$department_folder_path/dept_metrics.json")

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

        println("Updated Department Weekly-Trends DataFrame:")
        updatedDeptTrendDF.show(5)
        updatedDeptTrendDF.write.mode("overwrite").json(s"$metricsOutputDir/dept_trend_metrics.json")

      }
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("1 minute"))
      .start()

    streamingQuery.awaitTermination()
  }
}
