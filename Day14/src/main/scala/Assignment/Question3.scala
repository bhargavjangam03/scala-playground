package Assignment

import org.apache.spark.sql.SparkSession

object Question3 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Even Numbers")
      .master("local[*]")
      .getOrCreate()

    val numbersRDD = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    val oddNumbersRDD = numbersRDD.filter(_ % 2 == 0)

    oddNumbersRDD.collect().foreach(println)

    spark.stop()
  }
}

