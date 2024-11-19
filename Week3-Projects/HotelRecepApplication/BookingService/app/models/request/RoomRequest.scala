package models.request

import play.api.libs.json.{Json, OFormat}

case class RoomRequest (
                 roomNo: Int,
                 suiteType: String,
                 occupied: Boolean,
                 pricePerDay: Double
               )

object RoomRequest {
  implicit val format: OFormat[RoomRequest] = Json.format[RoomRequest]
}

