package models
import play.api.libs.functional.syntax._
import play.api.libs.json._
case class GuestIdentityProof(
                               id: Option[Long] = None,
                               guestId: Long,
                               identityProof: Array[Byte]
                             )

// JSON reads and writes for Visitor
object GuestIdentityProof {
  implicit val guestIdentityProofReads: Reads[GuestIdentityProof] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "visitorId").read[Long] and
      (JsPath \ "identityProof").read[Array[Byte]]
    )(GuestIdentityProof.apply _)

  implicit val guestIdentityProofWrites: Writes[GuestIdentityProof] = Json.writes[GuestIdentityProof]

  implicit val guestIdentityProofFormat: Format[GuestIdentityProof] = Format(guestIdentityProofReads, guestIdentityProofWrites)
}
