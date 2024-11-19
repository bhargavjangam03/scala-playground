package models.request
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Visitor(
                    visitorId: Option[Int] = None,
                    name: String,
                    email: String,
                    contactNumber: String
                  )

object Visitor {
  implicit val visitorReads: Reads[Visitor] = Json.reads[Visitor]

  implicit val visitorWrites: Writes[Visitor] = Json.writes[Visitor]

  implicit val visitorFormat: Format[Visitor] = Format(visitorReads, visitorWrites)
}
