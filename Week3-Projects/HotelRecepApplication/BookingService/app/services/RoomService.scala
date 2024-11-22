package services

import models.Room
import repositories.RoomRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomService @Inject()(roomRepository: RoomRepository)(implicit ec: ExecutionContext) {

  def addRoom(room: Room): Future[Int] = {
    roomRepository.addRoom(room)
  }

  def getRoomById(roomId: Int): Future[Option[Room]] = {
    roomRepository.getRoomById(roomId)
  }

  def getRoomByRoomNo(roomNo: Int): Future[Option[Room]] = {
    roomRepository.getRoomByRoomNo(roomNo)
  }

  def getAvailableRooms: Future[Seq[Room]] = {
    roomRepository.getAvailableRooms
  }

  def getAvailableRoomsBySuiteType(suiteType: String): Future[Seq[Room]] = {
    roomRepository.getAvailableRoomsBySuiteType(suiteType)
  }

  def updateRoomOccupiedStatus(roomNo: Int, occupied: Boolean): Future[Int] = {
    roomRepository.updateRoomOccupiedStatus(roomNo, occupied)
  }

  def getRoomIdByRoomNo(roomNo: Int): Future[Int] = {
    roomRepository.getRoomIdByRoomNo(roomNo)
  }

  def updateRoomPrice(roomNo: Int, newPrice: Double): Future[Int] = {
    roomRepository.updateRoomPrice(roomNo, newPrice)
  }

  def updateRoomDetails(roomId: Int, updatedRoom: Room): Future[Int] = {
    roomRepository.updateRoomDetails(roomId, updatedRoom)
  }

  def updateRoomByRoomNo(roomNo: Int, updatedRoom: Room): Future[Int] = {
    roomRepository.updateRoomByRoomNo(roomNo, updatedRoom)
  }

  def deleteRoom(roomNo: Int): Future[Int] = {
    roomRepository.deleteRoom(roomNo)
  }
}
