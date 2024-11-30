package Exercise4
import org.apache.spark.sql.SparkSession

object Exercise4 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise4")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/scala/spark-gcs-key.json")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val inputPath = "gs://bhargav-assignments/Day18And19Task/case_study_4/input"
    val df = spark.read.parquet(inputPath)

    println("Original DataFrame:")
    df.show()

    // Filter rows where status = "completed"
    val filteredDF = df.filter($"status" === "completed")

    println("Filtered DataFrame:")
    filteredDF.show()

    // Write the filtered DataFrame back to GCS
    val outputPath = "gs://bhargav-assignments/Day18And19Task/case_study_4/output"
    filteredDF.write.parquet(outputPath)

    println(s"Filtered Parquet file successfully written to $outputPath")

    spark.stop()
  }
}
