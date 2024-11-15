package models.request

import models.GuestData
import play.api.libs.json.{Json, Reads}

import java.time.LocalDate

case class GuestAllocationRequest(
                                   roomNo: Int,
                                   guests: Seq[GuestData],
                                   endDate: LocalDate
)
object GuestAllocationRequest {
  implicit val guestAllocationRequestReads: Reads[GuestAllocationRequest] = Json.reads[GuestAllocationRequest]
}

