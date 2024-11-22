package Assignment

import org.apache.spark.sql.SparkSession

object Question5 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Average Score")
      .master("local[*]")
      .getOrCreate()

    val scoreData = Seq(
      (1, 85),
      (2, 9),
      (3, 78),
      (4, 88),
      (5, 92)
    )

    val scoreRDD = spark.sparkContext.parallelize(scoreData)

    val totalScores = scoreRDD.map(_._2).sum()
    val count = scoreRDD.count()

    val averageScore = totalScores / count

    println(s"Average Score: $averageScore")

    spark.stop()
  }
}

