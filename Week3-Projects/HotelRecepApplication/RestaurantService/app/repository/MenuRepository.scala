package repository

import models.Menu
import models.db.MenuTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MenuRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._
  val menus = TableQuery[MenuTable]

  // Method to add a menu item
  def addMenu(menu: Menu): Future[Int] = db.run {
    (menus.map(m => (m.foodItem, m.foodType, m.price, m.date))
      returning menus.map(_.id.get)) += (menu.foodItem, menu.foodType, menu.price, menu.date)
  }

  // Method to fetch all menu items for a specific date
  def getFoodItemsByDate(date: LocalDate): Future[Seq[Menu]] = {
    val action = menus.filter(_.date === date)
    db.run(action.result)
  }

  // Method to fetch all menu items
  def getAllMenuItems(): Future[Seq[Menu]] = {
    db.run(menus.result)
  }

  // Method to fetch a menu item by ID
  def getMenuItemById(id: Int): Future[Option[Menu]] = {
    db.run(menus.filter(_.id === id).result.headOption)
  }

  // Method to update a menu item by ID
  def updateMenuItem(id: Int, updatedMenu: Menu): Future[Int] = db.run {
    menus.filter(_.id === id)
      .map(m => (m.foodItem, m.foodType, m.price, m.date))
      .update((updatedMenu.foodItem, updatedMenu.foodType, updatedMenu.price, updatedMenu.date))
  }

  // Method to delete a menu item by ID
  def deleteMenuItem(id: Int): Future[Int] = db.run {
    menus.filter(_.id === id).delete
  }
}


