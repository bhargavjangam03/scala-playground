package models

import models.request.RoomCheckoutRequest
import play.api.libs.json.{Json, Reads}

case class GuestData(
                      name: String,
                      email: String,
                      address: String,
                      idProof: String,
                      guestStatus: String
 )
object GuestData {
  implicit val guestDataReads: Reads[GuestData] = Json.reads[GuestData]
}


