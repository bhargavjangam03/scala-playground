package models.request

import play.api.libs.json.{Json, Reads, Writes, Format}

import java.time.LocalDate

case class GuestAllocationRequest(
                                   roomNo: Int,
                                   guests: Seq[GuestData],
                                   endDate: LocalDate,
                                   paymentStatus: String,
)
object GuestAllocationRequest {
  implicit val guestAllocationRequestReads: Reads[GuestAllocationRequest] = Json.reads[GuestAllocationRequest]

  implicit val guestAllocationRequestWrites: Writes[GuestAllocationRequest] = Json.writes[GuestAllocationRequest]

  implicit val guestAllocationRequestFormat: Format[GuestAllocationRequest] = Format(guestAllocationRequestReads, guestAllocationRequestWrites)
}

