package repository

import models.Guest
import models.db.GuestTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GuestRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val guests = TableQuery[GuestTable]

  def getActiveGuests: Future[Seq[Guest]] = db.run {
    guests.filter(_.status === "checked-in").result
  }

}
