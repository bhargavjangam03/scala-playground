package services

import repositories.BookingInfoRepository

import javax.inject.Inject


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate
import models.BookingInfo

@Singleton
class BookingInfoService @Inject()(bookingInfoRepository: BookingInfoRepository)(implicit ec: ExecutionContext) {

  // Method to add a new booking
  def addBooking(booking: BookingInfo): Future[Int] = {
    bookingInfoRepository.addBooking(booking)
  }

  // Method to get a booking by bookingId
  def getBookingById(bookingId: Int): Future[Option[BookingInfo]] = {
    bookingInfoRepository.getBookingById(bookingId)
  }

  // Method to get all bookings for a specific guest
  def getBookingsByGuestId(guestId: Long): Future[Seq[BookingInfo]] = {
    bookingInfoRepository.getBookingsByGuestId(guestId)
  }

  // Method to get all bookings for a specific room
  def getBookingsByRoomId(roomId: Int): Future[Seq[BookingInfo]] = {
    bookingInfoRepository.getBookingsByRoomId(roomId)
  }

  // Method to update the payment status of a booking
  def updatePaymentStatus(bookingId: Int, newStatus: String): Future[Int] = {
    bookingInfoRepository.updatePaymentStatus(bookingId, newStatus)
  }

  // Method to get all bookings within a specific date range
  def getBookingsByDateRange(startDate: LocalDate, endDate: LocalDate): Future[Seq[BookingInfo]] = {
    bookingInfoRepository.getBookingsByDateRange(startDate, endDate)
  }

  // Method to update the booking details (excluding bookingId)
  def updateBookingDetails(bookingId: Int, updatedBooking: BookingInfo): Future[Int] = {
    bookingInfoRepository.updateBookingDetails(bookingId, updatedBooking)
  }

}

