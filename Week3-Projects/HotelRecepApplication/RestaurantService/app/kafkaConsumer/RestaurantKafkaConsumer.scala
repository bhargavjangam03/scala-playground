package kafkaConsumer

import models.{Guest, GuestInfo}
import org.apache.pekko.Done
import org.apache.pekko.actor.CoordinatedShutdown
import play.api.Logging
import play.api.libs.json.{Format, Json}
import service.MenuService

import java.time.{Duration, LocalDate}
import java.util.Properties
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}
import org.apache.kafka.clients.consumer.KafkaConsumer
import models.Menu
import org.apache.kafka.common.serialization.StringDeserializer
import utils.MailUtil.composeAndSendEmail

@Singleton
class RestaurantKafkaConsumer @Inject()(menuService: MenuService, coordinatedShutdown: CoordinatedShutdown) extends Logging {

  logger.info("Starting RestaurantKafkaConsumer")
  implicit val GuestFormat: Format[Guest] = Json.format[Guest]
  private  val properties = new Properties()
  properties.put("bootstrap.servers", "10.128.0.2:9092")
  properties.put("group.id", s"restaurantService")
  properties.put("key.deserializer", classOf[StringDeserializer])
  properties.put("value.deserializer", classOf[StringDeserializer])

  private val stopConsumer: AtomicBoolean = new AtomicBoolean(false)
  private val executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
  private val mailExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3))
  val kafkaConsumer = new KafkaConsumer[String, String](properties)
  kafkaConsumer.subscribe(Set("Taj-Guest-List").asJava)

  Future {
    while(!stopConsumer.get()) {
      kafkaConsumer.poll(Duration.ofSeconds(3)).asScala.foreach(r => {
        logger.info(s"RestaurantKafkaConsumer receive record $r")
        val sendMenuToMail = composeAndSendEmail(Json.parse(r.value()).as[GuestInfo], _: Seq[Menu])
        menuService.getFoodItemsByDate(LocalDate.now()).map(sendMenuToMail)(mailExecutionContext)
      })
    }
    logger.info(s"SampleKafkaConsumer quits 'while(true)' loop.")
  }(executionContext)
    .andThen(_ => kafkaConsumer.close())(executionContext)
    .andThen {
      case Success(_) =>
        logger.info(s"KafkaConsumer succeed.")
      case Failure(e) =>
        logger.error(s"KafkaConsumer fails.", e)
    }(executionContext)

  coordinatedShutdown.addTask(CoordinatedShutdown.PhaseServiceStop, "RestaurantKafkaConsumer-stop"){() =>
    logger.info("Shutdown-task[RestaurantKafkaConsumer-stop] starts.")
    stopConsumer.set(true)
    Future{ Done }(executionContext).andThen{
      case Success(_) => logger.info("Shutdown-task[RestaurantKafkaConsumer-stop] succeed.")
      case Failure(e) => logger.error("Shutdown-task[RestaurantKafkaConsumer-stop] fails.", e)
    }(executionContext)
  }
}