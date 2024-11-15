package services

import repositories.RoomRepository

import javax.inject.Inject

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import models.Room

@Singleton
class RoomService @Inject()(roomRepository: RoomRepository)(implicit ec: ExecutionContext) {

  // Method to add a new room to the database
  def addRoom(room: Room): Future[Int] = {]
    roomRepository.addRoom(room)
  }

  // Method to get a room by its roomId
  def getRoomById(roomId: Int): Future[Option[Room]] = {
    roomRepository.getRoomById(roomId)
  }

  // Method to get a room by its roomNo
  def getRoomByRoomNo(roomNo: Int): Future[Option[Room]] = {
    roomRepository.getRoomByRoomNo(roomNo)
  }

  // Method to get all available rooms
  def getAvailableRooms: Future[Seq[Room]] = {
    roomRepository.getAvailableRooms
  }

  // Method to get available rooms by suite type
  def getAvailableRoomsBySuiteType(suiteType: String): Future[Seq[Room]] = {
    roomRepository.getAvailableRoomsBySuiteType(suiteType)
  }

  // Method to update the occupancy status of a room
  def updateRoomOccupiedStatus(roomNo: Int, occupied: Boolean): Future[Int] = {
    roomRepository.updateRoomOccupiedStatus(roomNo, occupied)
  }

  // Method to get the room number (roomNo) for a specific roomId
  def getRoomNoById(roomId: Int): Future[Option[Int]] = {
    roomRepository.getRoomNoById(roomId)
  }

  // Method to get the room ID for a specific roomNo
  def getRoomIdByRoomNo(roomNo: Int): Future[Option[Int]] = {
    roomRepository.getRoomIdByRoomNo(roomNo)
  }

  // Method to update the price for a room
  def updateRoomPrice(roomNo: Int, newPrice: Double): Future[Int] = {
    roomRepository.updateRoomPrice(roomNo, newPrice)
  }

  // Method to update room details (excluding roomId)
  def updateRoomDetails(roomId: Int, updatedRoom: Room): Future[Int] = {
    roomRepository.updateRoomDetails(roomId, updatedRoom)
  }
}
