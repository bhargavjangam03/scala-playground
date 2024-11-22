package controllers

import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import service.MenuService
import models.Menu

import java.time.LocalDate
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

  // Get all menu items for a specific date
  def getFoodItemsByDate(date: String): Action[AnyContent] = Action.async {
    val parsedDate = LocalDate.parse(date)
    menuService.getFoodItemsByDate(parsedDate).map { menuItems =>
      Ok(Json.toJson(menuItems))
    }
  }

  // Get all menu items
  def getAllMenuItems: Action[AnyContent] = Action.async {
    menuService.getAllMenuItems.map { menuItems =>
      Ok(Json.toJson(menuItems))
    }
  }

  // Get a menu item by ID
  def getMenuItemById(id: Int): Action[AnyContent] = Action.async {
    menuService.getMenuItemById(id).map {
      case Some(menuItem) => Ok(Json.toJson(menuItem))
      case None => NotFound(Json.obj("error" -> s"Menu item with ID $id not found"))
    }
  }

  // Update a menu item by ID
  def updateMenuItem(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Menu].fold(
      errors => Future.successful(BadRequest("Invalid menu data")),
      updatedMenu => {
        menuService.updateMenuItem(id, updatedMenu).map {
          case 1 => Ok(Json.obj("message" -> s"Menu item with ID $id updated"))
          case 0 => NotFound(Json.obj("error" -> s"Menu item with ID $id not found"))
        }
      }
    )
  }

  // Delete a menu item by ID
  def deleteMenuItem(id: Int): Action[AnyContent] = Action.async {
    menuService.deleteMenuItem(id).map {
      case 1 => Ok(Json.obj("message" -> s"Menu item with ID $id deleted"))
      case 0 => NotFound(Json.obj("error" -> s"Menu item with ID $id not found"))
    }
  }
}

