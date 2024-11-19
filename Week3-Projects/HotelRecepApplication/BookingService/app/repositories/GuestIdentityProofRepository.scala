package repositories

import models.GuestIdentityProof
import models.db.GuestIdentityProofTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GuestIdentityProofRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  private val guestIdentity = TableQuery[GuestIdentityProofTable]
  import dbConfig._
  import profile.api._

  def create(guestIdentityProof: GuestIdentityProof): Future[Int] = {
    val insertQueryThenReturnId = guestIdentity
      .map(v => (v.guestId, v.identityProof))
      .returning(guestIdentity.map(_.id))  // Ensure this returns a Long value

    // Execute the query and return the inserted visitor's ID
    db.run(insertQueryThenReturnId += (
      guestIdentityProof.guestId,
      guestIdentityProof.identityProof
    )).map(_.head)  // Extract the first element from the result (the ID)
  }

  def list(): Future[Seq[GuestIdentityProof]] = db.run(guestIdentity.result)

  def getById(id: Int): Future[Option[GuestIdentityProof]] = db.run(guestIdentity.filter(_.guestId === id).result.headOption)

}
