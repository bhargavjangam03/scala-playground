package services

import models.request.Employee
import repositories.EmployeeRepository

import javax.inject._
import scala.concurrent.Future

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository) {


  def create(employeeData: Employee): Future[Employee] = employeeRepository.create(employeeData)

  def isEmployeeEmailValid(email: String): Future[Boolean] = employeeRepository.isEmployeeEmailValid(email)

  def list(): Future[Seq[Employee]] = employeeRepository.list()

  def get(id: Int): Future[Option[Employee]] = employeeRepository.getById(id)

  def deleteEmployee(employeeId: Int): Future[Boolean] = {
    employeeRepository.deleteEmployee(employeeId)
  }
  def getEmployeeByEmail(mail: String): Future[Option[Employee]] = employeeRepository.getEmployeeByMail(mail)

}