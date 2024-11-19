package models.request

import play.api.libs.json.{Json, Reads}

case class GuestData(
                      name: String,
                      phoneNumber: String,
                      email: String,
                      address: String,
                      idProof: String
 )
object GuestData {
  implicit val guestDataReads: Reads[GuestData] = Json.reads[GuestData]
}


