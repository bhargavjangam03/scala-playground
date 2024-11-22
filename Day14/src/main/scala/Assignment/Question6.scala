package Assignment

import org.apache.spark.sql.SparkSession

object Question6 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Join RDDs")
      .master("local[*]")
      .getOrCreate()

    val nameData = Seq(
      (1, "Bhargav"),
      (2, "Saketh"),
      (3, "Teja-bhAAi"),
      (4, "Deva-Salaar")
    )

    val scoreData = Seq(
      (1, 99),
      (2, 90),
      (3, 94),
      (4, 100)
    )

    val nameRDD = spark.sparkContext.parallelize(nameData)
    val scoreRDD = spark.sparkContext.parallelize(scoreData)

    val joinedRDD = nameRDD.join(scoreRDD)

    val result = joinedRDD.map {
      case (id, (name, score)) => (id, name, score)
    }

    result.collect().foreach(println)

    spark.stop()
  }
}
