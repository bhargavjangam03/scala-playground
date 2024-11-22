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

  private val rooms = TableQuery[RoomTable]

  def addRoom(room: Room): Future[Int] = db.run {
    (rooms.map(r => (r.roomNo, r.suiteType, r.occupied, r.pricePerDay))
      returning rooms.map(_.roomId.get)) += (room.roomNo, room.suiteType, room.occupied, room.pricePerDay)
  }

  def getRoomById(roomId: Int): Future[Option[Room]] = db.run {
    rooms.filter(_.roomId === roomId).result.headOption
  }

  def getRoomByRoomNo(roomNo: Int): Future[Option[Room]] = db.run {
    rooms.filter(_.roomNo === roomNo).result.headOption
  }

  def getAvailableRooms: Future[Seq[Room]] = db.run {
    rooms.filter(_.occupied === false).result
  }

  def getAvailableRoomsBySuiteType(suiteType: String): Future[Seq[Room]] = db.run {
    rooms.filter(r => r.occupied === false && r.suiteType === suiteType).result
  }

  def updateRoomOccupiedStatus(roomNo: Int, occupied: Boolean): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.occupied).update(occupied)
  }

  def getRoomIdByRoomNo(roomNo: Int): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.roomId).result.headOption
  }.map {
    case Some(Some(roomId)) => roomId
    case _ => throw new NoSuchElementException(s"Room with roomNo $roomNo not found.")
  }

  def updateRoomPrice(roomNo: Int, newPrice: Double): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).map(_.pricePerDay).update(newPrice)
  }

  def updateRoomDetails(roomId: Int, updatedRoom: Room): Future[Int] = db.run {
    rooms.filter(_.roomId === roomId)
      .map(r => (r.roomNo, r.suiteType, r.occupied, r.pricePerDay))
      .update((updatedRoom.roomNo, updatedRoom.suiteType, updatedRoom.occupied, updatedRoom.pricePerDay))
  }

  def updateRoomByRoomNo(roomNo: Int, updatedRoom: Room): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo)
      .map(r => (r.roomNo, r.suiteType, r.occupied, r.pricePerDay))
      .update((updatedRoom.roomNo, updatedRoom.suiteType, updatedRoom.occupied, updatedRoom.pricePerDay))
  }

  def deleteRoom(roomNo: Int): Future[Int] = db.run {
    rooms.filter(_.roomNo === roomNo).delete
  }
}
