import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.util.Random
import java.util.concurrent.atomic.AtomicBoolean

object AsyncAwait {

  def main(args: Array[String]): Unit = {
    val futureResult: Future[String] = RandomNumberThreadExecutor()

    futureResult.onComplete {
      case Success(result) => println(s"Async operation completed successfully with result: $result")
      case Failure(exception) => println(s"Async operation failed with exception: $exception")
    }

    // Wait for the future to complete
    Thread.sleep(15000)
  }

  def RandomNumberThreadExecutor(): Future[String] = {
    val promise = Promise[String]()
    val stopFlag = new AtomicBoolean(false) // Atomic boolean for thread-safe stopping

    // Thread logic
    def createThread(threadName: String): Thread = {
      new Thread(new Runnable {
        def run(): Unit = {
          val random = new Random()
          while (!stopFlag.get()) { // Check if the flag is false
            val randomNumber = 1500 + random.nextInt(100) // Generate random number between 0 and 1999
            println(s"$threadName generated $randomNumber") // Optional: Print the generated number
            if (randomNumber == 1567) {
              promise.success(s"$threadName has generated 1567")
              stopFlag.set(true) // Set the flag to true to notify other threads to stop
            }
            Thread.sleep(500) // Sleep to slow down the generation
          }
        }
      })
    }

    // Create and start the threads
    val firstThread = createThread("firstThread")
    val secondThread = createThread("secondThread")
    val thirdThread = createThread("thirdThread")

    firstThread.start()
    secondThread.start()
    thirdThread.start()

    promise.future // Returning the future associated with the promise
  }
}
