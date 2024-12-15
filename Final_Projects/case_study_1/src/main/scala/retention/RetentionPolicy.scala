package retention

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path}

object RetentionPolicy {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Retention Policy")
      .config("spark.hadoop.fs.defaultFS", "gs://bhargav-assignments/")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/resources/spark-gcs-key.json")
      .config("spark.hadoop.fs.gs.auth.service.account.debug", "false")
      .master("local[*]")
      .getOrCreate()


    val gsProcessingTimeBasedRawDataParentPath = "gs://bhargav-assignments/final_project/case_study_1/raw/sensor-data/"

    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")
    val cutOffTime = LocalDateTime.now().minus(7, ChronoUnit.DAYS)

    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    val yearPaths = fs.listStatus(new Path(gsProcessingTimeBasedRawDataParentPath)).map(_.getPath)
    deleteOldFolders(yearPaths, fs, cutOffTime, formatter)

    spark.stop()
  }

  private def deleteOldFolders(yearPaths: Seq[Path], fs: FileSystem, cutOffTime: LocalDateTime, formatter: DateTimeFormatter): Unit = {
    val paths = yearPaths
      .iterator
      .flatMap(yearPath =>
        fs.listStatus(yearPath)
          .iterator
          .flatMap(monthPath =>
            fs.listStatus(monthPath.getPath)
              .iterator
              .flatMap(dayPath =>
                fs.listStatus(dayPath.getPath)
                  .iterator
                  .map(hourPath => (yearPath, monthPath, dayPath, hourPath))
              )
          )
      )

    paths.filter { case (yearPath, monthPath, dayPath, hourPath) =>
        val fullPath = s"${yearPath.getName}/${monthPath.getPath.getName}/${dayPath.getPath.getName}/${hourPath.getPath.getName}"
        val folderTimestamp = LocalDateTime.parse(fullPath, formatter)
        folderTimestamp.isBefore(cutOffTime)
      }
      .foreach { case (_, _, _, hourPath) =>
        val path = hourPath.getPath
        fs.delete(path, true)
      }
  }
}
