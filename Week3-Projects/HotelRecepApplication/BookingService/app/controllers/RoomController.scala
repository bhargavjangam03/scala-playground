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
    request.body.validate[Room].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> s"Invalid room data: ${errors.mkString}"))),
      room => {
        roomService.addRoom(room).map { roomId =>
          Created(Json.obj("message" -> s"Room added with ID $roomId"))
        }
      }
    )
  }

  def getAvailableRoomsByType(roomType: String): Action[AnyContent] = Action.async {
    roomService.getAvailableRoomsBySuiteType(roomType).map { rooms =>
      Ok(Json.toJson(rooms))
    }
  }

  def updateRoomByRoomNo(roomNo: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Room].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> s"Invalid room data: ${errors.mkString}"))),
      updatedRoom => {
        roomService.updateRoomByRoomNo(roomNo, updatedRoom).map { rowsAffected =>
          if (rowsAffected > 0) {
            Ok(Json.obj("message" -> s"Room with roomNo $roomNo updated successfully"))
          } else {
            NotFound(Json.obj("error" -> s"Room with roomNo $roomNo not found"))
          }
        }
      }
    )
  }

  def deleteRoom(roomNo: Int): Action[AnyContent] = Action.async {
    roomService.deleteRoom(roomNo).map { rowsAffected =>
      if (rowsAffected > 0) {
        Ok(Json.obj("message" -> s"Room with roomNo $roomNo deleted successfully"))
      } else {
        NotFound(Json.obj("error" -> s"Room with roomNo $roomNo not found"))
      }
    }
  }

}
