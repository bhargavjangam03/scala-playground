package Assignment

import org.apache.spark.sql.SparkSession

object Question8 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Filter Age")
      .master("local[*]")
      .getOrCreate()

    val csvData = Seq(
      "Bhargav,24",
      "Saketh,26",
      "Teja-bhAAi,27",
      "Srinija,16",
      "Priyanshi,14",
       "Deva-Salaar,30"
    )

    val rdd = spark.sparkContext.parallelize(csvData)

    val filteredRDD = rdd
      .map(line => {
        val parts = line.split(",")
        (parts(0), parts(1).toInt)
      })
      .filter(_._2 >= 18)

    filteredRDD.collect().foreach(println)

    spark.stop()
  }
}

