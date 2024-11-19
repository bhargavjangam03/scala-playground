package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class VisitorIdentityProof(
                    id: Option[Int] = None,
                    visitorId: Int,
                    identityProof: Array[Byte]
                  )

// JSON reads and writes for Visitor
object VisitorIdentityProof {
  implicit val visitorReads: Reads[VisitorIdentityProof] = Json.reads[VisitorIdentityProof]

  implicit val visitorWrites: Writes[VisitorIdentityProof] = Json.writes[VisitorIdentityProof]

  implicit val visitorFormat: Format[VisitorIdentityProof] = Format(visitorReads, visitorWrites)
}