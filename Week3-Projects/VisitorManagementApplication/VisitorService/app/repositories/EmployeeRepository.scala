package repositories

import models.db.EmployeeTable
import models.request.Employee
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}



class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._


  private val employees = TableQuery[EmployeeTable]

  //  def create(Employee: Employee): Future[Int] = db.run(Employees += Employee)
  def create(employee: Employee): Future[Employee] = {
    val insertQueryThenReturnId = employees
      .map(v => (v.employeeName, v.organisation, v.building, v.email, v.contactNo))
      .returning(employees.map(_.employeeId))  // Get the auto-generated employeeId

    // Execute the insert query and then fetch the inserted employee object
    db.run(insertQueryThenReturnId += (
      employee.employeeName,
      employee.organisation,
      employee.building,
      employee.email,
      employee.contactNo
    )).flatMap { insertedId =>
      // After inserting, fetch the full Employee object
      val fetchedEmployeeQuery = employees.filter(_.employeeId === insertedId).result.headOption

      // Fetch the employee based on the inserted ID
      db.run(fetchedEmployeeQuery).map {
        case Some(fetchedEmployee) => fetchedEmployee
        case None => throw new Exception("Employee not found after insertion")
      }
    }
  }

  def isEmployeeEmailValid(email: String): Future[Boolean] = {
    db.run(employees.filter(_.email === email).exists.result)
  }

  def list(): Future[Seq[Employee]] = db.run(employees.result)

  def getById(id: Int): Future[Option[Employee]] = db.run(employees.filter(_.employeeId === id).result.headOption)

  def getEmployeeByMail(mail: String): Future[Option[Employee]] = db.run(employees.filter(_.email === mail).result.headOption)


  def deleteEmployee(employeeId: Int): Future[Boolean] = {
    val query = employees.filter(_.employeeId === employeeId).delete
    db.run(query).map {
      case 0 => false
      case _ => true
    }
  }

}
