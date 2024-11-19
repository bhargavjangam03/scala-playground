package controllers

import models.request.Employee
import play.api.mvc._
import services.EmployeeService
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(
                                    val cc: ControllerComponents,
                                    employeeService: EmployeeService
                                  )(implicit ec: ExecutionContext) extends AbstractController(cc) {


  def addEmployee(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Employee] match {
      case JsSuccess(employee, _) =>
        employeeService.create(employee).map { created =>
          Created(Json.toJson(created))  // Return the full Employee object
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid Employee data",
          "errors" -> JsError.toJson(errors)
        )))
    }
  }


  def list(): Action[AnyContent] = Action.async{
    employeeService.list().map(employees => Ok(Json.toJson(employees)))
  }

  def getEmployeeDetails(EmployeeId: Long): Action[AnyContent] = Action.async{
    employeeService.get(EmployeeId).map {
      case Some(employee) => Ok(Json.toJson(employee))
      case None => NotFound(Json.obj("message" -> s"Employee with id $EmployeeId not found"))
    }
  }

  def deleteEmployee(employeeId: Long): Action[AnyContent] = Action.async {
    employeeService.deleteEmployee(employeeId).map {
      case true => Ok(s"Employee with ID $employeeId deleted successfully.")
      case false => NotFound(s"Invalid employee ID: $employeeId.")
    }
  }
}