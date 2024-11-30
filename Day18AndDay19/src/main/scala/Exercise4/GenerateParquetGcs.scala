package Exercise4

import org.apache.spark.sql.SparkSession

object GenerateParquetGcs {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Generate Parquet in GCS")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/scala/spark-gcs-key.json")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // Sample data for Parquet file
    val data = Seq(
      (1, "Alice", "completed"),
      (2, "Bob", "pending"),
      (3, "Charlie", "completed"),
      (4, "David", "in_progress"),
      (5, "Eva", "completed"),
      (6, "Frank", "pending"),
      (7, "Grace", "completed"),
      (8, "Hank", "in_progress"),
      (9, "Ivy", "completed"),
      (10, "Jack", "pending"),
      (11, "Karen", "completed"),
      (12, "Leo", "in_progress"),
      (13, "Mona", "completed"),
      (14, "Nick", "pending"),
      (15, "Oscar", "completed"),
      (16, "Pam", "completed"),
      (17, "Quinn", "in_progress"),
      (18, "Rose", "pending"),
      (19, "Sam", "completed"),
      (20, "Tina", "completed"),
      (21, "Uma", "pending"),
      (22, "Victor", "completed"),
      (23, "Wendy", "in_progress"),
      (24, "Xavier", "completed"),
      (25, "Yara", "pending"),
      (26, "Zara", "completed"),
      (27, "Adam", "in_progress"),
      (28, "Bella", "completed"),
      (29, "Chris", "pending"),
      (30, "Diana", "completed"),
      (31, "Eve", "completed"),
      (32, "Frank", "in_progress"),
      (33, "Grace", "pending"),
      (34, "Hank", "completed"),
      (35, "Ivy", "completed"),
      (36, "Jack", "pending"),
      (37, "Karen", "completed"),
      (38, "Leo", "in_progress"),
      (39, "Mona", "completed"),
      (40, "Nick", "completed"),
      (41, "Oscar", "pending"),
      (42, "Pam", "completed"),
      (43, "Quinn", "completed"),
      (44, "Rose", "pending"),
      (45, "Sam", "completed"),
      (46, "Tina", "in_progress"),
      (47, "Uma", "completed"),
      (48, "Victor", "pending"),
      (49, "Wendy", "completed"),
      (50, "Xavier", "completed")
    )


    val df = data.toDF("id", "name", "status")

    val outputPath = "gs://bhargav-assignments/Day18And19Task/case_study_4/input"
    df.write.parquet(outputPath)

    println(s"Parquet file successfully written to $outputPath")

    spark.stop()
  }
}

