package controllers


import models.VisitorIdentityProof
import models.request.{KafkaMessage, Visitor, VisitorLog}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{Action, _}
import services.{EmployeeService, KafkaProducerService, VisitorLogService, VisitorService}
import play.api.libs.json._
import utils.Validation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VisitorController @Inject()(
                                   val cc: ControllerComponents,
                                   visitorService: VisitorService,
                                   employeeService: EmployeeService,
                                   visitorLogService : VisitorLogService,
                                   kafkaProducerService: KafkaProducerService,
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {


  def checkInVisitor(): Action[MultipartFormData[TemporaryFile]] = Action.async(parse.multipartFormData) { request =>
    val name = request.body.dataParts.get("name").flatMap(_.headOption).getOrElse("")
    val hostName = request.body.dataParts.get("hostName").flatMap(_.headOption).getOrElse("")
    val hostMail = request.body.dataParts.get("hostMail").flatMap(_.headOption).getOrElse("")
    val email = request.body.dataParts.get("email").flatMap(_.headOption).getOrElse("")
    val contactNumber = request.body.dataParts.get("contactNumber").flatMap(_.headOption).getOrElse("")

    if (name.isEmpty || hostName.isEmpty || hostMail.isEmpty || email.isEmpty || contactNumber.isEmpty) {
      Future.successful(BadRequest(Json.obj("message" -> "Missing required fields")))
    } else {
      Validation.validateEmail(email) match {
        case Some(error) =>
          Future.successful(BadRequest(Json.obj("message" -> error.message)))
        case None =>
          Validation.validateContactNumber(contactNumber) match {
            case Some(error) =>
              Future.successful(BadRequest(Json.obj("message" -> error.message)))
            case None =>
              request.body.file("identityProof") match {
                case Some(filePart) =>
                  val filename = filePart.filename.toLowerCase
                  if (!filename.endsWith(".png")) {
                    Future.successful(BadRequest(Json.obj("message" -> "Only .png files are accepted as identity proof")))
                  } else {
                    val file = filePart.ref
                    val byteArray = java.nio.file.Files.readAllBytes(file.path)

                    employeeService.getEmployeeByEmail(hostMail).flatMap {
                      case Some(employee) =>
                        visitorService.getVisitorByEmail(email).flatMap {
                          case Some(existingVisitor) =>
                            val visitorLog = VisitorLog(
                              visitorId = existingVisitor.visitorId.get,
                              employeeId = employee.employeeId.getOrElse(0),
                              checkInTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                              status = "pending"
                            )
                            visitorLogService.addVisitorLog(visitorLog).map { _ =>
                              Ok(Json.toJson(s"Check-in logged successfully for $name. Waiting for $hostName confirmation."))
                            }
                          case None =>
                            val newVisitor = Visitor(
                              name = name,
                              email = email,
                              contactNumber = contactNumber,
                            )
                            visitorService.checkIn(newVisitor).flatMap { createdVisitorId =>
                              val visitorIdentity = VisitorIdentityProof(
                                visitorId = createdVisitorId,
                                identityProof = byteArray
                              )
                              val visitorLog = VisitorLog(
                                visitorId = createdVisitorId,
                                employeeId = employee.employeeId.getOrElse(0),
                                checkInTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                                status = "pending"
                              )
                              for {
                                _ <- visitorService.addVisitorIdentity(visitorIdentity)
                                _ <- visitorLogService.addVisitorLog(visitorLog)
                              } yield {
                                Ok(Json.toJson(s"Check-in and identity proof added successfully for $name. Waiting for $hostName confirmation."))
                              }
                            }
                        }
                      case None =>
                        Future.successful(BadRequest(Json.obj("message" -> "Host not found in the system")))
                    }
                  }
                case None =>
                  Future.successful(BadRequest(Json.obj("message" -> "Missing identity proof file")))
              }
          }
      }
    }
  }




  //  def test() : Action[AnyContent] = Action.async {
//    // Create a sample Visitor object
//    val sample = VisitorLog(
//      visitorId = 123,
//      employeeId = 456,
//      checkInTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
//      status = "pending"
//    )
//
//    // Send the Visitor object to Kafka asynchronously and return a Future
//    kafkaProducerService.sendToKafka(sample)
//      // Once the message is successfully sent to Kafka, return the success response
//    Future.successful(Ok("Visitor approved successfully."))
//  }


  def approveVisitor(visitorId: Int): Action[AnyContent] = Action.async {
    visitorLogService.updateVisitorLogStatus(visitorId, "approved").map {
      case Some(_) => Ok("Visitor approved successfully.")
      case None => InternalServerError("Failed to approve the visitor.")
    }
  }

  def rejectVisitor(visitorId: Int): Action[AnyContent] = Action.async {
    visitorLogService.updateVisitorLogStatus(visitorId, "rejected").map {
      case Some(_) => Ok("Visitor rejected successfully.")
      case None => InternalServerError("Failed to reject the visitor.")
    }
  }

  def checkOutVisitor(visitorId: Int): Action[AnyContent] = Action.async {
    visitorLogService.updateCheckOut(visitorId).map {
      case Some(_) => Ok("Visitor checked out successfully.")
      case None => InternalServerError("Failed to check out the visitor.")
    }
  }


  def list(): Action[AnyContent] = Action.async{
    visitorService.list().map(visitors => Ok(Json.toJson(visitors)))
  }

  def getVisitorDetails(visitorId: Int): Action[AnyContent] = Action.async{
    visitorService.getVisitorById(visitorId).map {
      case Some(visitor) => Ok(Json.toJson(visitor))
      case None => NotFound(Json.obj("message" -> s"visitor with id $visitorId not found"))
    }
  }
}