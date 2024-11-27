package Exercise5

import org.apache.spark.sql.SparkSession

object Exercise5 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise5")
      .master("local[*]")
      .getOrCreate()

    val filePath = "src/main/scala/Exercise5/1M_Sales_Records.csv"
    val rawRDD = spark.sparkContext.textFile(filePath)

    val header = rawRDD.first()
    val dataRDD = rawRDD.filter(row => row != header)

    val profitRDD = dataRDD.map(row => {
      val cols = row.split(",")
      (cols(cols.length - 1).toDouble, row)
    })


    def processPartitions(numPartitions: Int): Unit = {
      println(s"Processing with $numPartitions partitions:")

      val partitionedRDD = profitRDD.repartition(numPartitions)

      val rowCount = partitionedRDD.count()
      println(s"Number of rows: $rowCount")

      val sortedRDD = partitionedRDD.sortByKey()

      val outputPath = s"src/main/scala/Exercise5/output_sorted_$numPartitions"
      sortedRDD.map(_._2).saveAsTextFile(outputPath)

      println(s"Data sorted and saved to $outputPath")
    }

    List(2, 4, 8).foreach(processPartitions)

    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()

    spark.stop()
  }
}

