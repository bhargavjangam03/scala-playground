package models.db
import slick.jdbc.MySQLProfile.api._

import java.time.LocalDate
import models.GuestIdentityProof

class GuestIdentityProofTable(tag: Tag) extends Table[GuestIdentityProof](tag, "visitor_identity_proof") {
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
  def guestId = column[Long]("guest_id")
  def identityProof = column[Array[Byte]]("identity_proof")
  def * = (id, guestId, identityProof) <> ((GuestIdentityProof.apply _).tupled, GuestIdentityProof.unapply)
}
