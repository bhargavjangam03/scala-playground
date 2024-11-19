package model

import play.api.libs.json.{Format, JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class KafkaMessage(visitorId: Int,
                        visitorName: String,
                        employeeName: String,
                        visitorMail: String,
                        employeeMail: String,
                        visitorContactNumber:String,
                        visitorStatus:String)

object KafkaMessage {
  implicit val kafkaMessageReads: Reads[KafkaMessage] = Json.reads[KafkaMessage]
  implicit val kafkaMessageWrites: Writes[KafkaMessage] = Json.writes[KafkaMessage]

  implicit val kafkaMessageFormat: Format[KafkaMessage] = Format(kafkaMessageReads, kafkaMessageWrites)
}
