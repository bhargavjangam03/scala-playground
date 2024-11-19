package repositories

import models.Room
import models.db.RoomTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RoomRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // Query the rooms table
  private val rooms = TableQuery[RoomTable]

  // Method to add a new room to the database
  def addRoom(room: Room): Future[Int] = db.run {
    (rooms.map(r => (r.roomNo, r.suiteType, r.occupied, r.pricePerDay))
      returning rooms.map(_.roomId.get)) += (room.roomNo, room.suiteType, room.occupied, room.pricePerDay)
  }

  // Method to get a room by roomId
  def getRoomById(roomId: Int): Future[Option[Room]] = db.run {
    rooms.filter(_.roomId === roomId).result.headOption
  }

  // Method to get a room by roomNo
  def getRoomByRoomNo(roomNo: Int): Future[Option[Room]] = db.run {
    rooms.filter(_.roomNo === roomNo).result.headOption
  }

  // Method to get all available rooms
  def getAvailableRooms: Future[Seq[Room]] = db.run {
    rooms.filter(_.occupied === false).result
  }

  // Method to get available rooms by suite type
  def getAvailableRoomsBySuiteType(suiteType: String): Future[Seq[Room]] = db.run {
    rooms.filter(r => r.occupied === false && r.suiteType === suiteType).result
  }

  // Method to update the occupancy status of a room
  def updateRoomOccupiedStatus(roomNo: Int, occupied: Boolean): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.occupied).update(occupied)
  }


  // Method to get the room ID for a specific roomNo
  def getRoomIdByRoomNo(roomNo: Int): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.roomId).result.headOption
  }.map {
    case Some(Some(roomId)) => roomId
    case _ => throw new NoSuchElementException(s"Room with roomNo $roomNo not found.")
  }

  // Method to update the price for a room
  def updateRoomPrice(roomNo: Int, newPrice: Double): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.pricePerDay).update(newPrice)
  }

  // Method to update room details (excluding roomId)
  def updateRoomDetails(roomId: Int, updatedRoom: Room): Future[Int] = db.run {
    rooms.filter(_.roomId === roomId)
      .map(r => (r.roomNo, r.suiteType, r.occupied, r.pricePerDay))
      .update((updatedRoom.roomNo, updatedRoom.suiteType, updatedRoom.occupied, updatedRoom.pricePerDay))
  }
}
