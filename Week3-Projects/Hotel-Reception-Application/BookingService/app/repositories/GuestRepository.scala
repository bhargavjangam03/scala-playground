package repositories

import models.Guest
import models.db.GuestTable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

class GuestRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._



  private val guests = TableQuery[GuestTable]

  // Method to add guests to the database
  def addGuestsAndReturnIds(guestList: Seq[Guest]): Future[Seq[Long]] = {
    val addGuestsAction = guestList.map(guest => (guests returning guests.map(_.guestId)) += guest)
    db.run(DBIO.sequence(addGuestsAction).transactionally)
  }

  def findGuestsByRoomNo(roomNo: Int): Future[Seq[Guest]] = db.run {
    guests.filter(_.roomNo === roomNo).result
  }
  def updateGuestStatus(guestId: Long, status: String): Future[Int] = db.run {
    guests.filter(_.guestId === guestId).map(_.guestStatus).update(status)
  }

  def updateGuestsStatusByRoomNo(roomNo: Int, status: String): Future[Int] = db.run {
    guests.filter(_.roomNo === roomNo).map(_.guestStatus).update(status)
  }

  def getActiveGuests: Future[Seq[Guest]] = db.run {
    guests.filter(_.guestStatus === "ACTIVE").result
  }
}
