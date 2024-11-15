package services


import repositories.GuestRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import models.Guest

@Singleton
class GuestService @Inject()(guestRepository: GuestRepository)(implicit ec: ExecutionContext) {

  // Method to add guests and return their IDs
  def addGuestsAndReturnIds(guestList: Seq[Guest]): Future[Seq[Long]] = {
    guestRepository.addGuestsAndReturnIds(guestList)
  }

  // Method to find guests by room number
  def findGuestsByRoomNo(roomNo: Int): Future[Seq[Guest]] = {
    guestRepository.findGuestsByRoomNo(roomNo)
  }

  // Method to update guest status by guestId
  def updateGuestStatus(guestId: Long, status: String): Future[Int] = {
    guestRepository.updateGuestStatus(guestId, status)
  }

  // Method to update guest status by room number
  def updateGuestsStatusByRoomNo(roomNo: Int, status: String): Future[Int] = {
    guestRepository.updateGuestsStatusByRoomNo(roomNo, status)
  }

  // Method to get all active guests
  def getActiveGuests: Future[Seq[Guest]] = {
    guestRepository.getActiveGuests
  }
}
