package models.db

import models.Guest

import slick.jdbc.MySQLProfile.api._

class GuestTable(tag: Tag) extends Table[Guest](tag, "Guest") {
  def guestId = column[Int]("guest_id", O.PrimaryKey, O.AutoInc)
  def bookingId = column[Int]("booking_id")
  def name = column[String]("name")
  def roomNo = column[Int]("room_no")
  def phoneNumber = column[String]("phone_number")
  def email = column[String]("email")
  def address = column[String]("address")
  def status = column[String]("guest_status")

  def * = (guestId, bookingId, name, roomNo, phoneNumber, email, address, status) <> ((Guest.apply _).tupled, Guest.unapply)
}
