package models.db
import models.request.VisitorLog
import slick.jdbc.MySQLProfile.api._

class VisitorLogTable(tag: Tag) extends Table[VisitorLog](tag, "visitor_log") {
  def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
  def visitorId = column[Int]("visitor_id")
  def employeeId = column[Int]("employee_id")
  def checkInTime = column[String]("check_in_time")
  def checkOutTime = column[Option[String]]("check_out_time")
  def status = column[String]("status")

  def * = (id, visitorId, employeeId, checkInTime, checkOutTime, status) <> ((VisitorLog.apply _).tupled, VisitorLog.unapply)
}

