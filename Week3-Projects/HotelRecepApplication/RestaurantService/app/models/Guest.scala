package models

import play.api.libs.json.{Json, OFormat}

case class Guest(
                  guestId: Int,
                  bookingId: Int,
                  name: String,
                  roomNo: Int,
                  phoneNumber: String,
                  email: String,
                  address: String,
                  status: String
                )

object Guest {
  implicit val format: OFormat[Guest] = Json.format[Guest]
}