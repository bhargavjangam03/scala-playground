package Exercise3

import org.apache.spark.sql.SparkSession

object Exercise3 {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Exercise3")
      .master("local[*]")
      .config("spark.executor.instances", "2")
      .getOrCreate()

    //spark.executor.instances doesn't have any effect on local

    val loremIpsumText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    val linesRDD = spark.sparkContext.parallelize(Seq.fill(1000000)(loremIpsumText))

    val wordCountRDD = linesRDD
      .flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    wordCountRDD.take(10).foreach(println)

    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()

    spark.stop()
  }
}
