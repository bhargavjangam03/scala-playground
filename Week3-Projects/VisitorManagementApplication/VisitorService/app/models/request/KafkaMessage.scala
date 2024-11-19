package models.request
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Json, Reads, Writes}

case class KafkaMessage(visitorId: Int,
                        visitorName: String,
                        employeeName: String,
                        visitorMail: String,
                        employeeMail: String,
                        visitorContactNumber:String,
                        visitorStatus:String)

object KafkaMessage {
  implicit val kafkaMessageReads: Reads[KafkaMessage] = (
    (JsPath \ "visitorId").read[Int] and
      (JsPath \ "visitorName").read[String] and
      (JsPath \ "employeeName").read[String] and
      (JsPath \ "visitorMail").read[String] and
      (JsPath \ "employeeMail").read[String] and
      (JsPath \ "visitorContactNumber").read[String] and
      (JsPath \ "visitorStatus").read[String]
    )(KafkaMessage.apply _)

  implicit val kafkaMessageWrites: Writes[KafkaMessage] = Json.writes[KafkaMessage]

  implicit val kafkaMessageFormat: Format[KafkaMessage] = Format(kafkaMessageReads, kafkaMessageWrites)
}
