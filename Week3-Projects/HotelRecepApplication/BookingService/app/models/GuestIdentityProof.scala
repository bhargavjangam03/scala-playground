package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
case class GuestIdentityProof(
                               id: Option[Int] = None,
                               guestId: Int,
                               identityProof: String
                             )

// JSON reads and writes for Visitor
object GuestIdentityProof {
  implicit val guestIdentityProofReads: Reads[GuestIdentityProof] = (
    (JsPath \ "id").readNullable[Int] and
      (JsPath \ "visitorId").read[Int] and
      (JsPath \ "identityProof").read[String]
    )(GuestIdentityProof.apply _)

  implicit val guestIdentityProofWrites: Writes[GuestIdentityProof] = Json.writes[GuestIdentityProof]

  implicit val guestIdentityProofFormat: Format[GuestIdentityProof] = Format(guestIdentityProofReads, guestIdentityProofWrites)
}
