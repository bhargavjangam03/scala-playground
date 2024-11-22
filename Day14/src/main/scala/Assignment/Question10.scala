package Assignment

import org.apache.spark.sql.SparkSession

object Question10 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Group and Sum by Key")
      .master("local[*]")
      .getOrCreate()

    val data = Seq(
      ("a", 10),
      ("b", 20),
      ("a", 30),
      ("b", 40),
      ("c", 50)
    )

    val rdd = spark.sparkContext.parallelize(data)

    val summedByKey = rdd
      .groupByKey()
      .mapValues(_.sum)

    summedByKey.collect().foreach(println)

    spark.stop()
  }
}
