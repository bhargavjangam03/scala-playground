package services

import models.Employee
import repositories.EmployeeRepository

import javax.inject._
import scala.concurrent.Future

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository) {


  def create(employeeData: Employee): Future[Long] = employeeRepository.create(employeeData)

  def isEmployeeEmailValid(email: String): Future[Boolean] = employeeRepository.isEmployeeEmailValid(email)

  def list(): Future[Seq[Employee]] = employeeRepository.list()

  def get(id: Long): Future[Option[Employee]] = employeeRepository.getById(id)

  //  def create(Visitor: Visitor): Future[Long] = VisitorRepository.create(Visitor)

  //  def update(id: Long, Visitor: Visitor): Future[Option[Visitor]] =
  //    VisitorRepository.update(id, Visitor)
  //
  //  def delete(id: Long): Future[Boolean] = VisitorRepository.delete(id)
}
