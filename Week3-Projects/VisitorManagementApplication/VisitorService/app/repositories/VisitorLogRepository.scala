package repositories


import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models.db.VisitorLogTable
import models.request.Visitor

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import models.request.VisitorLog


class VisitorLogRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  private val visitorLogs = TableQuery[VisitorLogTable]

  // Add a new visitor log entry
  def addVisitorLog(visitorLog: VisitorLog): Future[Int] = {
    val query = visitorLogs += visitorLog
    db.run(query)
  }

  // Update the status of a visitor log entry
  def updateVisitorLogStatus(visitorId: Int, newStatus: String): Future[Option[VisitorLog]] = {
    val query = visitorLogs.filter(_.visitorId === visitorId).map(_.status).update(newStatus)

    db.run(query).flatMap { rowsAffected =>
      if (rowsAffected > 0) {
        // Fetch the updated visitor log after successful update
        db.run(visitorLogs.filter(_.visitorId === visitorId).result.headOption)
      } else {
        // No rows were updated; return None
        Future.successful(None)
      }
    }
  }


  def updateCheckOut(visitorId: Int): Future[Option[VisitorLog]] = {
    val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)  // Current time for checkOutTime
    val defaultStatus = "checked-out"  // Default status

    // Construct the update query
    val updateQuery = visitorLogs.filter(visitor => visitor.visitorId === visitorId && visitor.status === "checked-in")
      .map(v => (v.checkOutTime, v.status))
      .update((Some(currentTime), defaultStatus))  // Set checkOutTime to current time and status to "Checked Out"

    // Execute the update and handle the result
    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None)  // No rows updated, return None
      case _ =>
        // After updating, fetch the updated visitor to return it
        db.run(visitorLogs.filter(_.visitorId === visitorId).result.headOption)
    }
  }

}
