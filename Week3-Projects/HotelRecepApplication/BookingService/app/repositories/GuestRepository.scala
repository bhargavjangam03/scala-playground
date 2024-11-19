package repositories

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

  def addGuests(guestList: Seq[Guest]): Future[Seq[Int]] = {
    val addGuestsAction = guestList.map(guest => guests returning guests.map(_.guestId) += guest)
    db.run(DBIO.sequence(addGuestsAction).transactionally)
  }

  def findGuestsByRoomNo(roomNo: Int): Future[Seq[Guest]] = db.run {
    guests.filter(_.roomNo === roomNo).result
  }

  def updateGuestsStatusByRoomNo(roomNo: Int, status: String): Future[Int] = db.run {
    guests.filter(_.roomNo === roomNo).map(_.status).update(status)
  }

  def getActiveGuests: Future[Seq[Guest]] = db.run {
    guests.filter(_.status === "checked-in").result
  }
}
