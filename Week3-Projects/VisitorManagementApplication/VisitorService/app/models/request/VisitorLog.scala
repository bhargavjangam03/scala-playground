package models.request

import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class VisitorLog(
                     id: Option[Int] = None,
                    visitorId: Int,
                    employeeId: Int,
                    checkInTime: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),  // Default to current time
                    checkOutTime: Option[String] = None,
                    status: String
                  )

// JSON reads and writes for Visitor
object VisitorLog {
  implicit val visitorLogReads: Reads[VisitorLog] = Json.reads[VisitorLog]

  implicit val visitorLogWrites: Writes[VisitorLog] = Json.writes[VisitorLog]

  implicit val visitorLogFormat: Format[VisitorLog] = Format(visitorLogReads, visitorLogWrites)
}
