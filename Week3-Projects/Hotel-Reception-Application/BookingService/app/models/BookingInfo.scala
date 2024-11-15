package models

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat}

case class BookingInfo(
                        bookingId: Int,
                        guestId: Long,
                        roomId: Int,
                        startDate: LocalDate,
                        endDate: LocalDate,
                        totalAmount: Int,
                        paymentStatus: String
                      )

object BookingInfo {
  implicit val format: OFormat[BookingInfo] = Json.format[BookingInfo]
}

