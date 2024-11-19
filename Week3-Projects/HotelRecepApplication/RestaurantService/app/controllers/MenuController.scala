package controllers

import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import service.MenuService
import models.Menu
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class MenuController @Inject()(cc: ControllerComponents, menuService: MenuService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def addMenu(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Menu].fold(
      errors => Future.successful(BadRequest("Invalid menu data")),
      menu => {
        menuService.addMenu(menu).map { id =>
          Created(s"Menu item added with ID: $id")
        }
      }
    )
  }

}
