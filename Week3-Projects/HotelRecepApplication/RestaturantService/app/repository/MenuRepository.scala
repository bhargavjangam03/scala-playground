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
      returning menus.map(_.id)) += (menu.foodItem, menu.foodType, menu.price, menu.date)
  }

  def getFoodItemsByDate(date: LocalDate): Future[Seq[Menu]] = {
    val action = menus.filter(_.date === date)
    db.run(action.result)
  }

}

