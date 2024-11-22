package services

import com.google.inject.Inject
import repositories.VisitorLogRepository

import javax.inject.Singleton
import models.request.{KafkaMessage, VisitorLog}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class VisitorLogService @Inject()(visitorLogRepository: VisitorLogRepository,
                                  visitorService:VisitorService,
                                  employeeService:EmployeeService,
                                  kafkaProducerService: KafkaProducerService
                              ) {

  // Add a new visitor log entry
  // Add a new visitor log entry
  // Add a new visitor log entry
  def addVisitorLog(visitorLog: VisitorLog): Future[Int] = {
    // Add the visitor log entry
    val persistedVisitorLogFuture = visitorLogRepository.addVisitorLog(visitorLog)

    // Fetch the visitor and employee details asynchronously
    persistedVisitorLogFuture.flatMap { visitorLogId =>
      for {
        visitorOption <- visitorService.getVisitorById(visitorLog.visitorId)
        employeeOption <- employeeService.getEmployeeById(visitorLog.employeeId)
      } yield {
        // Handle case when both visitor and employee are found
        (visitorOption, employeeOption) match {
          case (Some(visitor), Some(employee)) =>
            // Create the KafkaRequest when both visitor and employee are found
            val kafkaRequest = KafkaMessage(
              visitorId = visitorLogId,
              visitorName = visitor.name,
              employeeName = employee.employeeName,
              visitorMail = visitor.email,
              employeeMail = employee.email,
              visitorContactNumber = visitor.contactNumber,
              visitorStatus = visitorLog.status
            )

            // Send the Kafka request
            kafkaProducerService.sendToKafka(kafkaRequest)
        }

        // Return the visitorLogId after processing Kafka request
        visitorLogId
      }
    }
  }




  // Update the status of a visitor log entry
  def updateVisitorLogStatus(visitorId: Int, newStatus: String): Future[Option[VisitorLog]] = {
    // Update the status in the repository
    visitorLogRepository.updateVisitorLogStatus(visitorId, newStatus).flatMap {
      case Some(visitorLog) =>
        // If the visitor log entry is updated, fetch the visitor and employee details
        for {
          visitorOption <- visitorService.getVisitorById(visitorLog.visitorId)
          employeeOption <- employeeService.getEmployeeById(visitorLog.employeeId)
        } yield {
          // Handle case when both visitor and employee are found
          (visitorOption, employeeOption) match {
            case (Some(visitor), Some(employee)) =>
              // Create the KafkaRequest with the updated details
              val kafkaRequest = KafkaMessage(
                visitorId = visitorLog.visitorId,
                visitorName = visitor.name,
                employeeName = employee.employeeName,
                visitorMail = visitor.email,
                employeeMail = employee.email,
                visitorContactNumber = visitor.contactNumber,
                visitorStatus = newStatus // Use the new status for Kafka
              )

              // Send the Kafka request
              kafkaProducerService.sendToKafka(kafkaRequest)
          }
          // Return the updated visitor log entry
          Some(visitorLog)
        }

      case None =>
        // Return None if no visitor log entry is updated
        Future.successful(None)
    }
  }

  // Update the check-out time and status for a visitor log entry
  def updateCheckOut(visitorId: Int): Future[Option[VisitorLog]] = {
    // Update the check-out time and status in the repository
    visitorLogRepository.updateCheckOut(visitorId).flatMap {
      case Some(visitorLog) =>
        // If the visitor log entry is updated, fetch the visitor and employee details
        for {
          visitorOption <- visitorService.getVisitorById(visitorLog.visitorId)
          employeeOption <- employeeService.getEmployeeById(visitorLog.employeeId)
        } yield {
          // Handle case when both visitor and employee are found
          (visitorOption, employeeOption) match {
            case (Some(visitor), Some(employee)) =>
              // Create the KafkaRequest with the updated details
              val kafkaRequest = KafkaMessage(
                visitorId = visitorLog.visitorId,
                visitorName = visitor.name,
                employeeName = employee.employeeName,
                visitorMail = visitor.email,
                employeeMail = employee.email,
                visitorContactNumber = visitor.contactNumber,
                visitorStatus = "checked-out" // Use the "checked-out" status
              )

              // Send the Kafka request
              kafkaProducerService.sendToKafka(kafkaRequest)
          }
          // Return the updated visitor log entry
          Some(visitorLog)
        }

      case None =>
        // Return None if no visitor log entry is updated
        Future.successful(None)
    }
  }

}

