package model

import spray.json.DefaultJsonProtocol._
import spray.json._


case class KafkaMessage(visitorId: Int,
                        visitorName: String,
                        employeeName: String,
                        visitorMail: String,
                        employeeMail: String,
                        visitorContactNumber:String,
                        visitorStatus:String)
object JsonFormats {
  implicit val kafkaMessageFormat: RootJsonFormat[KafkaMessage] = jsonFormat7(KafkaMessage)
}


