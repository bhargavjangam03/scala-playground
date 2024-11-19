package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class BookingInfo(
                        bookingId: Int,
                        roomNo: Int,
                        startDate: LocalDate,
                        endDate: LocalDate,
                        totalAmount: Int,
                        paymentStatus: String,
                        status: String,
                      )

object BookingInfo {
  implicit val format: OFormat[BookingInfo] = Json.format[BookingInfo]
}

