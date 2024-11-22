package Assignment

import org.apache.spark.sql.SparkSession

object Question7 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Union and Remove Duplicates")
      .master("local[*]")
      .getOrCreate()

    val rdd1 = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5, 8, 9))
    val rdd2 = spark.sparkContext.parallelize(Seq(3, 4, 5, 6, 7, 8, 10))

    val unionRDD = rdd1.union(rdd2).distinct()

    unionRDD.collect().foreach(println)

    spark.stop()
  }
}
