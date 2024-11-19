package models.db

import models.GuestIdentityProof
import slick.jdbc.MySQLProfile.api._

class GuestIdentityProofTable(tag: Tag) extends Table[GuestIdentityProof](tag, "visitor_identity_proof") {
  def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
  def guestId = column[Int]("guest_id")
  def identityProof = column[String]("identity_proof")
  def * = (id, guestId, identityProof) <> ((GuestIdentityProof.apply _).tupled, GuestIdentityProof.unapply)
}
