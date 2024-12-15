package controller


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object SensorController {
  implicit val system: ActorSystem = ActorSystem("SensorMetricsAPISystem")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val spark: SparkSession = SparkSession.builder()
    .appName("Sensor Controller APi")
    .config("spark.hadoop.fs.defaultFS", "gs://bhargav-assignments/")
    .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
    .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
    .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
    .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "src/main/resources/spark-gcs-key.json")
    .config("spark.hadoop.fs.gs.auth.service.account.debug", "false")
    .master("local[*]")
    .getOrCreate()

  val gsAggregationJsonDataParentPath = "gs://bhargav-assignments/final_project/case_study_1/aggregated/json"

  def main(args: Array[String]): Unit = {
    val route: Route = concat(
      path("api"/"sensor"/"aggregated") {
        get {
          onComplete(Future {
            fetchAggregatedMetrics(spark)
          }) {
            case Success(result) =>
              complete(HttpEntity(ContentTypes.`application/json`, result))
            case Failure(exception) =>
              complete(HttpResponse(
                status = StatusCodes.InternalServerError,
                entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, exception.getMessage)
              ))
          }
        }
      },
      path("api"/"sensor"/"aggregated"/ Segment) { sensorId =>
        get {
          onComplete(Future {
            fetchAggregatedMetricsBySensorId(spark, sensorId.toInt)
          }) {
            case Success(result) =>
              complete(HttpEntity(ContentTypes.`application/json`, result))
            case Failure(exception) =>
              complete(HttpResponse(
                status = StatusCodes.InternalServerError,
                entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, exception.getMessage)
              ))
          }
        }
      }
    )

    Http().newServerAt("0.0.0.0", 8080).bind(route)
    println("Server online at http://0.0.0.0:8080/")
  }

  def fetchAggregatedMetrics(spark: SparkSession): String = {
    getLatestAggregatedGCPFolder(spark) match {
      case Success(fp) =>
        Try(spark.read.json(fp)) match {
          case Success(df) => df.toJSON.collect().mkString("[", ",", "]")
          case Failure(_) => "[]"
        }
      case Failure(_) => "[]"
    }
  }

  def fetchAggregatedMetricsBySensorId(spark: SparkSession, sensorId: Int): String = {
    getLatestAggregatedGCPFolder(spark) match {
      case Success(fp) =>
        Try {
          spark.read.json(fp)
            .filter(col("sensorId") === sensorId)
            .toJSON
            .collect()
            .mkString("[", ",", "]")
        }.getOrElse("[]")
      case Failure(_) => "[]"
    }
  }

  def getLatestAggregatedGCPFolder(spark: SparkSession): Try[String] = Try {
    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    val basePathObj = new Path(gsAggregationJsonDataParentPath)

    require(fs.exists(basePathObj), "Base path does not exist") // Fail fast if base path doesn't exist

    def findLatestFolder(path: Path): Path = {
      val status = fs.listStatus(path)
      require(status.nonEmpty, s"No subdirectories found in $path")

      status
        .filter(_.isDirectory)
        .map(_.getPath)
        .maxBy(_.getName.toInt)
    }

    val latestPath = List(
      basePathObj,
      findLatestFolder(basePathObj),
      findLatestFolder(findLatestFolder(basePathObj)),
      findLatestFolder(findLatestFolder(findLatestFolder(basePathObj))),
      findLatestFolder(findLatestFolder(findLatestFolder(findLatestFolder(basePathObj))))
    ).last

    latestPath.toString
  }

}

