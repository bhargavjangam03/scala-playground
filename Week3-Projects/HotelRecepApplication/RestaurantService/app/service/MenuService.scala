package service

import models.Menu
import repository.MenuRepository

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MenuService @Inject()(menuRepository: MenuRepository)(implicit ec: ExecutionContext) {

  // Add a new menu item
  def addMenu(menu: Menu): Future[Int] = {
    menuRepository.addMenu(menu)
  }

  // Fetch all food items for a given date
  def getFoodItemsByDate(date: LocalDate): Future[Seq[Menu]] = {
    menuRepository.getFoodItemsByDate(date)
  }

  // Fetch all menu items
  def getAllMenuItems: Future[Seq[Menu]] = {
    menuRepository.getAllMenuItems()
  }

  // Fetch a menu item by ID
  def getMenuItemById(id: Int): Future[Option[Menu]] = {
    menuRepository.getMenuItemById(id)
  }

  // Update a menu item by ID
  def updateMenuItem(id: Int, updatedMenu: Menu): Future[Int] = {
    menuRepository.updateMenuItem(id, updatedMenu)
  }

  // Delete a menu item by ID
  def deleteMenuItem(id: Int): Future[Int] = {
    menuRepository.deleteMenuItem(id)
  }
}
