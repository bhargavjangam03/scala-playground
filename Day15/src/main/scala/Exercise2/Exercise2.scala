package Exercise2

import org.apache.spark.sql.SparkSession

object Exercise2 {

  def main(args: Array[String]):Unit = {

    val spark = SparkSession.builder().appName("Exercise2").master("local[*]").getOrCreate()

    val numbersRDD = spark.sparkContext.parallelize(1 to 1000)

    val mappedRDD = numbersRDD.map(number => (number % 10, number))

    val filteredRDD = mappedRDD.filter { case (_, number) => number > 500 }

    val groupedRDD = filteredRDD.groupByKey()

    val result = groupedRDD.collect()
    result.foreach { case (key, values) =>
      println(s"Key: $key, Values: ${values.mkString(", ")}")
    }
     groupedRDD.saveAsTextFile("src/main/scala/Exercise2/output_Exercise_2")

    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()
    // Stop the Spark session
    spark.stop()
  }

}
