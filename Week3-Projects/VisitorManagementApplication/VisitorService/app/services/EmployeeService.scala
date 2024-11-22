package services

import models.request.Employee
import repositories.EmployeeRepository

import javax.inject._
import scala.concurrent.{ExecutionContext,Future}

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository)(implicit ec: ExecutionContext) {


  def create(employeeData: Employee): Future[Employee] = employeeRepository.create(employeeData)

  def isEmployeeEmailValid(email: String): Future[Boolean] = employeeRepository.isEmployeeEmailValid(email)

  def list(): Future[Seq[Employee]] = employeeRepository.list()

  def getEmployeeById(id: Int): Future[Option[Employee]] = employeeRepository.getEmployeeById(id)

  def deleteEmployee(employeeId: Int): Future[Boolean] = {
    employeeRepository.deleteEmployee(employeeId)
  }
  def getEmployeeByEmail(mail: String): Future[Option[Employee]] = employeeRepository.getEmployeeByMail(mail)

  def updateEmployee(employeeId: Int, employee: Employee): Future[Option[Employee]] = {
    getEmployeeById(employeeId).flatMap {
      case Some(_) =>
        employeeRepository.updateEmployee(employeeId, employee).flatMap {
          case rowsUpdated if rowsUpdated > 0 =>
            getEmployeeById(employeeId)
          case _ =>
            Future.successful(None)
        }
      case None =>
        Future.successful(None)
    }
  }


}
