package Assignment

import org.apache.spark.sql.SparkSession

object Question1 {

  def main(args:Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Word Count")
      .master("local[*]")
      .getOrCreate()

    val sentenceCollection = Seq(
      "hallow",
      "How you doin!",
      "That's what she said",
      "Na dhaari raha dhaari",
      "ee bhasha okkasari chepthe vandha saarlu cheppinattu"
    )
    val sentenceRDD = spark.sparkContext.parallelize(sentenceCollection)

    val wordCount = sentenceRDD.flatMap(_.split(" ")).count()

    println(wordCount)
    spark.stop()
  }

}
