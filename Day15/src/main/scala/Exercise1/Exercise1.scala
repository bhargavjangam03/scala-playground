package Exercise1

import org.apache.spark.sql.SparkSession

object Exercise1 {

  def main(args: Array[String]):Unit = {

    val spark =  SparkSession.builder().appName("Exercise 1").master("local[*]").getOrCreate()

    val numOfElements =  10000000

    val initialRDD = spark.sparkContext.parallelize( 1 to numOfElements)

    println(s"Initial number of partitions: ${initialRDD.getNumPartitions}")
    // Equals to 12 which are number of cores in my mac

    initialRDD.mapPartitionsWithIndex{(index,iter) =>
      Iterator(s"Partition $index with size : ${iter.size}")
    }.collect().foreach(println)

    val repartitionedRDD = initialRDD.repartition(4)
    println(s"Number of partitions after repartitioning: ${repartitionedRDD.getNumPartitions}")

    println("After Repartitioning:")
    repartitionedRDD.mapPartitionsWithIndex { (index, iter) =>
      Iterator(s"Partition $index size: ${iter.size}")
    }.collect().foreach(println)

    val coalescedRDD = repartitionedRDD.coalesce(2)
    println(s"Number of partitions after coalescing: ${coalescedRDD.getNumPartitions}")

    println("After Coalescing:")
    coalescedRDD.mapPartitionsWithIndex { (index, iter) =>
      Iterator(s"Partition $index size: ${iter.size}")
    }.collect().foreach(println)

    println("First 5 elements in each partition:")
    coalescedRDD.mapPartitionsWithIndex { (index, iter) =>
      Iterator(s"Partition $index first 5 elements: ${iter.take(5).mkString(", ")}")
    }.collect().foreach(println)

    // Hold the Spark UI
    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()

    spark.stop()
  }

}
