package models.db
import slick.jdbc.MySQLProfile.api._
import models.BookingInfo

import java.time.LocalDate

// Define the BookingInfo table
class BookingInfoTable(tag: Tag) extends Table[BookingInfo](tag, "booking_info") {
  def bookingId = column[Int]("booking_id", O.PrimaryKey, O.AutoInc) // Auto-increment primary key
  def guestId = column[Long]("guest_id")
  def roomId = column[Int]("room_id")
  def startDate = column[LocalDate]("start_date")
  def endDate = column[LocalDate]("end_date")
  def totalAmount = column[Int]("total_amount")
  def paymentStatus = column[String]("payment_status")

  def * = (bookingId, guestId, roomId, startDate, endDate, totalAmount, paymentStatus) <> ((BookingInfo.apply _).tupled, BookingInfo.unapply)
}