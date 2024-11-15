package models

import play.api.libs.json.{Json, OFormat}

case class Room(
                 roomId: Int,
                 roomNo: Int,
                 suiteType: String,
                 occupied: Boolean,
                 pricePerDay: Double
               )

object Room {
  implicit val format: OFormat[Room] = Json.format[Room]
}
