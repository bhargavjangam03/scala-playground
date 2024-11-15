package controllers

import javax.inject._
import play.api.mvc._
import jobs.RoomStatusUpdateTask
import repositories.{BookingInfoRepository, GuestRepository, RoomRepository}
import services.{KafkaProducerService,BookingInfoService, GuestService, RoomService}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{JsValue, Json, Reads}
import models.{BookingInfo, Guest, GuestIdentityProof}
import models.request.GuestAllocationRequest
import play.api.Logging

import java.util.Base64
import java.time.LocalDate

@Singleton
class RoomController @Inject()(
                                val controllerComponents: ControllerComponents,
                                roomStatusUpdateTask: RoomStatusUpdateTask,
                                roomService: RoomService,
                                guestService: GuestService,
                                bookingInfoRepository: BookingInfoService,
                                kafkaProducerService: KafkaProducerService
                              )(implicit ec: ExecutionContext) extends BaseController with Logging {

  // Trigger manual execution of the room status update task
  def triggerRoomStatusUpdate: Action[AnyContent] = Action.async {
    roomStatusUpdateTask.checkAndUpdateRoomAndGuestStatus().map { _ =>
      Ok(Json.obj("message" -> "Cron job triggered manually"))
    }
  }

  // API to get available rooms by type
  def getAvailableRoomsByType(roomType: String): Action[AnyContent] = Action.async {
    roomService.getAvailableRoomsBySuiteType(roomType).map { rooms =>
      Ok(Json.toJson(rooms))
    }
  }


  def allocateRoom: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[GuestAllocationRequest].fold(
      errors => {
        val errorMessages = errors.map { case (path, validationErrors) =>
          s"${path.toString()}: ${validationErrors.map(_.message).mkString(", ")}"
        }
        Future.successful(BadRequest(Json.obj("message" -> "Invalid data", "errors" -> errorMessages)))
      },
      allocationRequest => {
        if (allocationRequest.guests.size > 3) {
          Future.successful(BadRequest(Json.obj("message" -> "Only up to 3 guests allowed per room")))
        } else {
          val guestsWithRoomNo = allocationRequest.guests.map { guestData =>
            val idProofBytes = Base64.getDecoder.decode(guestData.idProof)
            Guest(0, guestData.name, allocationRequest.roomNo, guestData.email, guestData.address, idProofBytes, guestData.guestStatus)
          }

          logger.info(s"Allocating room number: ${allocationRequest.roomNo} to ${allocationRequest.guests.size} guests.")

          // Step 1: Retrieve the actual RoomID from the Room table based on roomNo
          roomService.getRoomIdByRoomNo(allocationRequest.roomNo).flatMap {
            case Some(roomId) =>
              // Step 2: Proceed with the transaction only if RoomID exists
              val insertGuestsAndUpdateRoomAndBooking = for {
                guestIds <- guestService.addGuestsAndReturnIds(guestsWithRoomNo) // Insert guests and retrieve IDs
                _ <- Future.sequence(guestsWithRoomNo.zip(guestIds).map {
                  case (guest, guestId) =>
                    val identityProof = GuestIdentityProof(guestId, guest.idProof) // Create identity proof record
                    guestService.addGuestIdentityProof(identityProof) // Insert into GuestIdentityProof table
                }) // Insert identity proofs
                _ <- roomService.updateRoomStatusByRoomNo(allocationRequest.roomNo, "OCCUPIED") // Update room status
                _ <- bookingDetailsRepository.addBooking(BookingDetails(
                  bookingId = 0, // Auto-generated
                  guestId = guestIds.head, // Reference the first generated guest ID
                  roomId = roomId, // Use the actual RoomID
                  startDate = LocalDate.now(), // Set start_date to the current date
                  endDate = allocationRequest.endDate // Use provided end_date
                ))
              } yield ()

              // Send each guest's details to Kafka after they are added to the database
              val guestSendFutures = allocationRequest.guests.map { guest =>
                kafkaProducerService.sendGuestBookingMessage(guest.name, guest.email)
              }

              insertGuestsAndUpdateRoomAndBooking.flatMap { _ =>
                Future.sequence(guestSendFutures).map { _ =>
                  logger.info(s"Successfully allocated room number: ${allocationRequest.roomNo}.")
                  Ok(Json.obj("message" -> "Room allocated successfully"))
                }
              }.recover {
                case ex: Exception =>
                  logger.error(s"Failed to allocate room number: ${allocationRequest.roomNo}", ex)
                  InternalServerError(Json.obj("message" -> "Failed to allocate room"))
              }

            case None =>
              // If roomNo doesn't correspond to any RoomID in the database, return an error
              Future.successful(BadRequest(Json.obj("message" -> "Invalid room number")))
          }
        }
      }
    )
  }


  // API to check out guests by room number
  def checkoutGuest: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[RoomCheckoutRequest].fold(
      errors => {
        val errorMessages = errors.map { case (path, validationErrors) =>
          s"${path.toString()}: ${validationErrors.map(_.message).mkString(", ")}"
        }
        Future.successful(BadRequest(Json.obj("message" -> "Invalid data", "errors" -> errorMessages)))
      },
      checkoutRequest => {
        val roomNo = checkoutRequest.roomNo

        for {
          // Step 1: Retrieve all guests for the specified room number
          guests <- guestService.findGuestsByRoomNo(roomNo)

          // Step 2: Update guestStatus to "INACTIVE" for all guests in the room
          _ <- Future.sequence(guests.map(guest => guestService.updateGuestStatus(guest.guestId, "INACTIVE")))

          // Step 3: Update room status to "AVAILABLE"
          _ <- roomService.updateRoomStatusByRoomNo(roomNo, "AVAILABLE")

        } yield Ok(Json.obj("message" -> "Room checked out successfully"))
      }
    )
  }
}



