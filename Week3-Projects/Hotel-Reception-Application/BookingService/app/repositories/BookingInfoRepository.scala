package repositories


import models.BookingInfo
import models.db.BookingInfoTable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.time.LocalDate


class BookingInfoRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._



  // Query the booking_info table
  private val bookings = TableQuery[BookingInfoTable]

  // Method to add a new booking to the database
  def addBooking(booking: BookingInfo): Future[Int] = db.run {
    (bookings.map(b => (b.guestId, b.roomId, b.startDate, b.endDate, b.totalAmount, b.paymentStatus))
      returning bookings.map(_.bookingId)) += (booking.guestId, booking.roomId, booking.startDate, booking.endDate, booking.totalAmount, booking.paymentStatus)
  }

  // Method to get a booking by bookingId
  def getBookingById(bookingId: Int): Future[Option[BookingInfo]] = db.run {
    bookings.filter(_.bookingId === bookingId).result.headOption
  }

  // Method to get all bookings for a specific guest
  def getBookingsByGuestId(guestId: Long): Future[Seq[BookingInfo]] = db.run {
    bookings.filter(_.guestId === guestId).result
  }

  // Method to get all bookings for a specific room
  def getBookingsByRoomId(roomId: Int): Future[Seq[BookingInfo]] = db.run {
    bookings.filter(_.roomId === roomId).result
  }

  // Method to update payment status of a booking
  def updatePaymentStatus(bookingId: Int, newStatus: String): Future[Int] = db.run {
    bookings.filter(_.bookingId === bookingId).map(_.paymentStatus).update(newStatus)
  }

  // Method to get all bookings within a specific date range
  def getBookingsByDateRange(startDate: LocalDate, endDate: LocalDate): Future[Seq[BookingInfo]] = db.run {
    bookings.filter(b => b.startDate >= startDate && b.endDate <= endDate).result
  }

  // Method to update the booking details (excluding bookingId)
  def updateBookingDetails(bookingId: Int, updatedBooking: BookingInfo): Future[Int] = db.run {
    bookings.filter(_.bookingId === bookingId)
      .map(b => (b.guestId, b.roomId, b.startDate, b.endDate, b.totalAmount, b.paymentStatus))
      .update((updatedBooking.guestId, updatedBooking.roomId, updatedBooking.startDate, updatedBooking.endDate, updatedBooking.totalAmount, updatedBooking.paymentStatus))
  }
}

