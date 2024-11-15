package models.request
import play.api.libs.json.{Json, Reads}


case class RoomCheckoutRequest(roomNo: Int)
object RoomCheckoutRequest {
  // Define the implicit Reads for RoomCheckoutRequest
  implicit val roomCheckoutRequestReads: Reads[RoomCheckoutRequest] = Json.reads[RoomCheckoutRequest]
}

