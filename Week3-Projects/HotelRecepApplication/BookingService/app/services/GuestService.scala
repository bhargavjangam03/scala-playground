package services

import models.Guest
import repositories.GuestRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GuestService @Inject()(guestRepository: GuestRepository)(implicit ec: ExecutionContext) {

  // Method to add guests and return their IDs
  def addGuests(guestList: Seq[Guest]): Future[Seq[Int]] = {
    guestRepository.addGuests(guestList)
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
