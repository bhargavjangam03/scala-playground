package repositories

import models.db.VisitorTable
import models.request.Visitor
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class VisitorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._


  private val visitors = TableQuery[VisitorTable]

  def create(visitor: Visitor): Future[Int] = {
    val insertQueryThenReturnId = visitors
      .map(v => (v.name, v.email, v.contactNumber))
      .returning(visitors.map(_.visitorId))  // Ensure this returns a Int value

    // Execute the query and return the inserted visitor's ID
    db.run(insertQueryThenReturnId += (
      visitor.name,
      visitor.email,
      visitor.contactNumber,
    )).map(_.head)  // Extract the first element from the result (the ID)
  }

  def list(): Future[Seq[Visitor]] = db.run(visitors.result)

  def getVisitorById(id: Int): Future[Option[Visitor]] = db.run(visitors.filter(_.visitorId === id).result.headOption)

  def getVisitorByEmail(mail:String):Future[Option[Visitor]] = db.run(visitors.filter(_.email=== mail).result.headOption)

}

