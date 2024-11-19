package models.db

import models.request.Visitor
import slick.jdbc.MySQLProfile.api._

class VisitorTable(tag: Tag) extends Table[Visitor](tag, "visitors") {
  def visitorId = column[Option[Int]]("visitor_id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def contactNumber = column[String]("contact_number")

  def * = (visitorId, name, email, contactNumber) <> ((Visitor.apply _).tupled, Visitor.unapply)
}
