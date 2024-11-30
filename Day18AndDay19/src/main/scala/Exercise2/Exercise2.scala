package Exercise2

import org.apache.spark.sql.SparkSession

object Exercise2 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise2")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val salesData = Seq(
      (1, "ProductA", 100.0, "2024-01-01"),
      (2, "ProductB", 200.0, "2024-01-02"),
      (3, "ProductA", 150.0, "2024-01-03"),
      (4, "ProductC", 300.0, "2024-01-04"),
      (5, "ProductB", 250.0, "2024-01-05"),
      (6, "ProductA", 120.0, "2024-01-06"),
      (7, "ProductC", 350.0, "2024-01-07")
    ).toDF("sale_id", "product", "amount", "date")

    println("Sales Dataset:")
    salesData.show()

    // Simulate multiple transformations on the sales data
    val transformedData = salesData
      .filter($"amount" > 150)
      .groupBy("product")
      .agg(
        "amount" -> "sum"
      )
      .withColumnRenamed("sum(amount)", "total_sales")

    // Without caching, measure execution time for the first run
    val startTimeWithoutCache = System.nanoTime()
    val resultWithoutCache = transformedData.collect()
    val endTimeWithoutCache = System.nanoTime()

    println(s"Time taken without caching: ${(endTimeWithoutCache - startTimeWithoutCache) / 1e9} seconds")

    // Caching the transformed data
    transformedData.cache()

    // Re-run the same transformation with caching
    val startTimeWithCache = System.nanoTime()
    val resultWithCache = transformedData.collect()
    val endTimeWithCache = System.nanoTime()

    println(s"Time taken with caching: ${(endTimeWithCache - startTimeWithCache) / 1e9} seconds")

    spark.stop()
  }
}
