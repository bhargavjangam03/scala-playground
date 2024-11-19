package models.db

import models.Menu

import java.time.LocalDate

import slick.jdbc.MySQLProfile.api._

class MenuTable(tag: Tag) extends Table[Menu](tag, "menu") {

  def id = column[Int]("id", O.PrimaryKey)
  def foodItem = column[String]("food_item")
  def foodType = column[String]("food_type")
  def price = column[Double]("price")
  def date = column[LocalDate]("date")

  def * = (id, foodItem, foodType, price, date) <> ((Menu.apply _).tupled, Menu.unapply)

}
