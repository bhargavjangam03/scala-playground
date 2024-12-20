package controllers

import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import services.GuestService

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class GuestController @Inject()(
                                 val controllerComponents: ControllerComponents,
                                 val guestService: GuestService
                               )(implicit ec: ExecutionContext) extends BaseController with Logging {

  def getActiveGuests(): Action[AnyContent] = Action.async {
    guestService.getActiveGuests.map { guests =>
      // Filter only the required fields (name and email) and convert to JSON
      val guestInfo = guests.map(guest => Json.obj("name" -> guest.name, "email" -> guest.email))
      Ok(Json.obj("activeGuests" -> guestInfo))
    } recover {
      case ex: Exception =>
        logger.error("Error retrieving active guests", ex)
        InternalServerError(Json.obj("message" -> "Failed to retrieve active guests"))
    }
  }
}
