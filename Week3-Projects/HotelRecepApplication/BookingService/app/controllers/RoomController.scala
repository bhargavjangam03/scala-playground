package controllers

import models.Room
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.RoomService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomController @Inject()(val cc: ControllerComponents, roomService: RoomService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def addRoom(): Action[JsValue] = Action.async(parse.json) { request =>
    // Parse and validate the JSON body to a Room object
    request.body.validate[Room].fold(
      // If validation fails, return a BadRequest with error message
      errors => Future.successful(BadRequest(Json.obj("error" -> s"Invalid room data: ${errors.mkString}"))),
      // If validation is successful, call the service method to add the room
      room => {
        roomService.addRoom(room).map { roomId =>
          // Return a success message with the roomId
          Created(Json.obj("message" -> s"Room added with ID $roomId"))
        }
      }
    )
  }

  // API to get available rooms by type
  def getAvailableRoomsByType(roomType: String): Action[AnyContent] = Action.async {
    roomService.getAvailableRoomsBySuiteType(roomType).map { rooms =>
      Ok(Json.toJson(rooms))
    }
  }

}