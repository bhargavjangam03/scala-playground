package repositories

import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import models.db.GuestIdentityProofTable
import models.GuestIdentityProof

class GuestIdentityProofRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val guestIdentity = TableQuery[GuestIdentityProofTable]

  def create(visitorIdentity: GuestIdentityProof): Future[Long] = {
    val insertQueryThenReturnId = guestIdentity
      .map(v => (v.guestId, v.identityProof))
      .returning(guestIdentity.map(_.id))  // Ensure this returns a Long value

    // Execute the query and return the inserted visitor's ID
    db.run(insertQueryThenReturnId += (
      visitorIdentity.guestId,
      visitorIdentity.identityProof
    )).map(_.head)  // Extract the first element from the result (the ID)
  }

  def list(): Future[Seq[GuestIdentityProof]] = db.run(guestIdentity.result)

  def getById(id: Long): Future[Option[GuestIdentityProof]] = db.run(guestIdentity.filter(_.guestId === id).result.headOption)

}
