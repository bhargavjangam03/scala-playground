package Exercise4

import org.apache.spark.sql.SparkSession

object Exercise4 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Exercise4")
      .master("local[*]")
      .getOrCreate()

    val rdd = spark.sparkContext.parallelize(1 to 10000)

    val filteredRDD = rdd.filter(_ % 2 == 0)
    val mappedRDD = filteredRDD.map(_ * 10)
    val flatMappedRDD = mappedRDD.flatMap(x => Seq((x, 1), (x + 1, 1)))
    val reducedRDD = flatMappedRDD.reduceByKey(_ + _)

    val results = reducedRDD.collect()

    println("Sample results:")
    results.take(10).foreach(println)

    // Hold the Spark UI
    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()

    spark.stop()
  }
}

