package controllers

import models.BookingInfo
import utils.Validation
import models.request.GuestAllocationRequest
import models.{Room, Guest, GuestIdentityProof}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{BookingInfoService, GuestIdentityProofService, GuestService, KafkaProducerService, RoomService}

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookingController @Inject()(
                                   val controllerComponents: ControllerComponents,
                                   roomService: RoomService,
                                   guestService: GuestService,
                                   guestIdentityProofService: GuestIdentityProofService,
                                   bookingInfoService: BookingInfoService,
                                   kafkaProducerService: KafkaProducerService
                              )(implicit ec: ExecutionContext) extends BaseController with Logging {



  def checkIn: Action[JsValue] = Action.async(parse.json) { request =>
    request.body
      .validate[GuestAllocationRequest]
      .fold(
        errors => Future.successful(BadRequest(Json.obj("error" -> errors.mkString))),
        payload => handleRoomAllocation(payload)
      )
  }

  private def handleRoomAllocation(payload: GuestAllocationRequest): Future[Result] = {
    val roomNo = payload.roomNo
    if (payload.guests.size > 3) {
      return Future.successful(BadRequest(Json.obj("error" -> "Maximum number of guests is 3.")))
    }
    roomService.getRoomByRoomNo(roomNo).flatMap {
      case Some(room) if room.occupied =>
        Future.successful(Conflict(Json.obj("error" -> s"Room $roomNo is already occupied.")))

      case Some(room) =>
        val invalidGuests = payload.guests.filterNot { guest =>
          Validation.validateEmail(guest.email) && Validation.validatePhoneNumber(guest.phoneNumber)
        }

        if (invalidGuests.nonEmpty) {
          Future.successful(
            BadRequest(Json.obj("error" -> s"Invalid guest data: ${invalidGuests.mkString(", ")}"))
          )
        } else {
          allocateRoomForGuests(room, payload)
        }

      case None =>
        Future.successful(NotFound(Json.obj("error" -> s"Room $roomNo not found.")))
    }
  }

  private def allocateRoomForGuests(room: Room, payload: GuestAllocationRequest): Future[Result] = {
    // First, create the booking information (this will give us the bookingId)
    for {
      bookingId <- bookingInfoService.addBooking(
        BookingInfo(
          bookingId = 0,
          roomNo = room.roomNo,
          startDate = LocalDate.now(),
          endDate = payload.endDate,
          totalAmount = ((ChronoUnit.DAYS.between(LocalDate.now(), payload.endDate)) * room.pricePerDay).toInt,
          paymentStatus = payload.paymentStatus,
          status = "checked-in"
        )
      )
      // Then, create guest entries with the generated bookingId
      guests = payload.guests.map { guest =>
        Guest(
          guestId = 0, // Placeholder; DB will generate this
          bookingId = bookingId, // Associate the bookingId with each guest
          name = guest.name,
          roomNo = payload.roomNo,
          phoneNumber = guest.phoneNumber,
          email = guest.email,
          address = guest.address,
          status = "checked-in"
        )
      }
      guestIds <- guestService.addGuests(guests)
      _ <- Future.traverse(guestIds.zip(payload.guests)) { case (guestId, guest) =>
        guestIdentityProofService.create(GuestIdentityProof(None, guestId, guest.idProof))
      }
      _ <- roomService.updateRoomOccupiedStatus(payload.roomNo, occupied = true) // Mark the room as occupied
      _ <- Future.traverse(payload.guests) { guest =>
        kafkaProducerService.sendGuestBookingMessage(guest.name, guest.email)
      }
    } yield Ok(Json.obj("success" -> s"Room ${payload.roomNo} allocated with booking ID $bookingId"))
  }




def checkOut: Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "roomNo").asOpt[Int] match {
      case Some(roomNo) => handleRoomCheckout(roomNo)
      case None => Future.successful(BadRequest(Json.obj("error" -> "Missing roomNo.")))
    }
  }

  private def handleRoomCheckout(roomNo: Int): Future[Result] = {
    roomService.getRoomByRoomNo(roomNo).flatMap {
      case Some(room) if !room.occupied =>
        Future.successful(Conflict(Json.obj("error" -> s"Room $roomNo is not occupied.")))

      case Some(room) =>
        bookingInfoService.getCheckedInBookingByRoomNo(roomNo).flatMap {
          case Some(booking) if booking.paymentStatus != "completed" =>
            Future.successful(BadRequest(Json.obj("error" -> s"Please complete the payment for Room $roomNo.")))

          case Some(booking) =>
            // Mark guests as checked-out and update room status
            guestService.updateGuestsStatusByRoomNo(roomNo, "checked-out").flatMap { _ =>
              roomService.updateRoomOccupiedStatus(roomNo, occupied = false).flatMap { _ =>
                // Mark booking as checked out
                bookingInfoService.updateStatus(booking.bookingId,"checked-out").map { _ =>
                  Ok(Json.obj("success" -> s"Room $roomNo checked out successfully."))
                }
              }
            }

          case None =>
            Future.successful(NotFound(Json.obj("error" -> s"No booking found for Room $roomNo.")))
        }

      case None =>
        Future.successful(NotFound(Json.obj("error" -> s"Room $roomNo not found.")))
    }
  }

  def completePayment(bookingId: Int): Action[AnyContent] = Action.async {
    bookingInfoService.completePayment(bookingId).map {
      case 1 => Ok(Json.obj("message" -> s"Booking with ID $bookingId is now completed"))
      case 0 => NotFound(Json.obj("error" -> s"Booking with ID $bookingId not found"))
    }
  }

}



