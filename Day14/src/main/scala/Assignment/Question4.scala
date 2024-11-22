package Assignment

import org.apache.spark.sql.SparkSession

object Question4 {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Frequency Count")
      .master("local[*]")
      .getOrCreate()

    val sentenceCollection = Seq(
      "Shivaji The boss",
      "Chitti the robot",
      "Jailer",
      "Vettaiyan",
      "Hunter"
    )
    val sentenceRDD = spark.sparkContext.parallelize(sentenceCollection)


    val frequencies = sentenceRDD.flatMap(_.split(" ")).flatMap(_.toCharArray).map((_, 1))
      .reduceByKey(_ + _)
      .collect()

    frequencies.foreach(println)

    spark.stop()


  }
}
