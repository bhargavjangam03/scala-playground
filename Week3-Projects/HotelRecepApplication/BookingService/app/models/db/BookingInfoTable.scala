package models.db

import models.BookingInfo
import java.time.LocalDate
import slick.jdbc.MySQLProfile.api._

// Define the BookingInfo table
class BookingInfoTable(tag: Tag) extends Table[BookingInfo](tag, "booking_info") {
  def bookingId = column[Int]("booking_id", O.PrimaryKey, O.AutoInc) // Auto-increment primary key
  def roomNo = column[Int]("room_no")
  def startDate = column[LocalDate]("start_date")
  def endDate = column[LocalDate]("end_date")
  def totalAmount = column[Int]("total_amount")
  def paymentStatus = column[String]("payment_status")
  def status = column[String]("status")

  def * = (bookingId, roomNo, startDate, endDate, totalAmount, paymentStatus, status) <> ((BookingInfo.apply _).tupled, BookingInfo.unapply)
}