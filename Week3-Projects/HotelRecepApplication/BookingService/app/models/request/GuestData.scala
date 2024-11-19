package models.request

import play.api.libs.json.{Json, Reads, Writes, Format}

case class GuestData(
                      name: String,
                      phoneNumber: String,
                      email: String,
                      address: String,
                      idProof: String
 )
object GuestData {
  implicit val guestDataReads: Reads[GuestData] = Json.reads[GuestData]

  implicit val guestDataWrites: Writes[GuestData] = Json.writes[GuestData]

  implicit val guestDataFormat: Format[GuestData] = Format(guestDataReads,guestDataWrites)
}


