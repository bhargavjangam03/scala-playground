package Exercise1
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.broadcast

object Exercise1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise1 ")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val userDetails = Seq(
      (1, "Alice"),
      (2, "Bob"),
      (3, "Charlie"),
      (4, "David"),
      (5, "Eve"),
      (6, "Frank"),
      (7, "Grace"),
      (8, "Helen"),
      (9, "Ivy"),
      (10, "Jack")
    ).toDF("user_id", "user_name")

    val transactionLogs = Seq.tabulate(1000000)(i => (i % 10 + 1, s"2024-01-${(i % 31) + 1}", i * 10.0))
      .toDF("user_id", "transaction_date", "amount")

    println("Transaction Logs Dataset:")
    transactionLogs.show()

    println("User Details Dataset:")
    userDetails.show()

    // Join without broadcasting
    val startNonBroadcast = System.nanoTime()
    val nonBroadcastJoin = transactionLogs.join(userDetails, "user_id")
    nonBroadcastJoin.count() // Trigger action to measure time
    val endNonBroadcast = System.nanoTime()

    println(s"Time taken for join without broadcasting: ${(endNonBroadcast - startNonBroadcast) / 1e9} seconds")

    // Join with broadcasting
    val startBroadcast = System.nanoTime()
    val broadcastJoin = transactionLogs.join(broadcast(userDetails), "user_id")
    broadcastJoin.count() // Trigger action to measure time
    val endBroadcast = System.nanoTime()

    println(s"Time taken for join with broadcasting: ${(endBroadcast - startBroadcast) / 1e9} seconds")
    spark.stop()
  }
}
