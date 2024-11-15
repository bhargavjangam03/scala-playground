package models.db

import models.Room
import slick.jdbc.MySQLProfile.api._

class RoomTable(tag: Tag) extends Table[Room](tag, "Room") {
  def roomId = column[Int]("room_id", O.PrimaryKey, O.AutoInc) // Auto-increment primary key
  def roomNo = column[Int]("room_no")
  def suiteType = column[String]("suite_type") // suiteType as String
  def occupied = column[Boolean]("occupied")
  def pricePerDay = column[Double]("price_per_day")

  def * = (roomId, roomNo, suiteType, occupied, pricePerDay) <> ((Room.apply _).tupled, Room.unapply)
}
