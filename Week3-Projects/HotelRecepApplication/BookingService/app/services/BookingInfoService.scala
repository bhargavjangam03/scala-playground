package services

import models.BookingInfo
import repositories.BookingInfoRepository

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookingInfoService @Inject()(bookingInfoRepository: BookingInfoRepository)(implicit ec: ExecutionContext) {

  // Method to add a new booking
  def addBooking(booking: BookingInfo): Future[Int] = {
    bookingInfoRepository.addBooking(booking)
  }

  def getCheckedInBookingByRoomNo(roomNo: Int): Future[Option[BookingInfo]] = {
    bookingInfoRepository.getCheckedInBookingByRoomNo(roomNo)
  }

  // Method to update the payment status of a booking
  def updateStatus(bookingId: Int, newStatus: String): Future[Int] = {
    bookingInfoRepository.updateStatus(bookingId, newStatus)
  }


  // Method to get a booking by bookingId
  def getBookingById(bookingId: Int): Future[Option[BookingInfo]] = {
    bookingInfoRepository.getBookingById(bookingId)
  }

  // Method to get all bookings for a specific room
  def getBookingsByRoomNo(roomId: Int): Future[Seq[BookingInfo]] = {
    bookingInfoRepository.getBookingsByRoomNo(roomId)
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

  // Method to mark a booking as completed
  def completePayment(bookingId: Int): Future[Int] = {
    bookingInfoRepository.completePayment(bookingId)
  }


}

