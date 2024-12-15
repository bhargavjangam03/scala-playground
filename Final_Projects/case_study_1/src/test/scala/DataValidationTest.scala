

import utils.GcsOperations.validateSensorData
import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.types._
import org.scalatest.funsuite.AnyFunSuite

class DataValidationTest extends AnyFunSuite {

  val spark: SparkSession = SparkSession.builder()
    .appName("Sensor Data Validation Test")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  // Define the schema for the sensor data
  val sensorDataSchema: StructType = StructType(Array(
    StructField("sensorId", IntegerType, nullable = true),
    StructField("timestamp", LongType, nullable = true),
    StructField("temperature", FloatType, nullable = true),
    StructField("humidity", FloatType, nullable = true)
  ))

  test("validateSensorData") {
    // Sample data with some invalid rows
    val sensorData = Seq(
      Row(1, 1733960468159L, 120.46f, 80.76f),
      Row(1, 1733960468159L, -56.78f, 34.56f),  // Invalid temperature
      Row(2, 1733960468159L, 78.68f, 150.32f),  // Invalid humidity
      Row(3, null, 75.65f, 45.32f),              // Invalid timestamp
      Row(4, 1733960468159L, 200.00f, 40.00f),   // Invalid temperature
      Row(5, 1733960468159L, 23.45f, -5.00f)     // Invalid humidity
    )

    // Create DataFrame using the schema
    val sensorDataDF = spark.createDataFrame(spark.sparkContext.parallelize(sensorData), sensorDataSchema)

    // Apply validation method
    val validatedDF = validateSensorData(sensorDataDF)

    // Collect results after filtering
    val result = validatedDF.collect()

    // Assert that the validated data only includes valid records
    assert(result.length == 1, "The filtered data does not match the expected result.")

    // Additional checks for the validated data (e.g., checking temperature and humidity ranges)
    result.foreach { row =>
      assert(row.getAs[Float]("temperature") >= -50 && row.getAs[Float]("temperature") <= 150, "Temperature is out of valid range.")
      assert(row.getAs[Float]("humidity") >= 0 && row.getAs[Float]("humidity") <= 100, "Humidity is out of valid range.")
    }
  }
}