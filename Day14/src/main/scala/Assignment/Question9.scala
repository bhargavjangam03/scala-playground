package Assignment

import org.apache.spark.sql.SparkSession

object Question9 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Sum of Integers")
      .master("local[*]")
      .getOrCreate()

    val rdd = spark.sparkContext.parallelize(1 to 100)

    val sum = rdd.sum()

    println(s"Sum of integers from 1 to 100: $sum")

    spark.stop()
  }
}

