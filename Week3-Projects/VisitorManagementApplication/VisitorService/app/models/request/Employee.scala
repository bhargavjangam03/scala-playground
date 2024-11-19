package models.request

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Employee(employeeId: Option[Int],
                    employeeName: String,
                    organisation: String,
                    building: String,
                    email: String,
                    contactNo: String)

object Employee {
  // Reads for deserialization
  implicit val employeeReads: Reads[Employee] = Json.reads[Employee]

  // Writes for serialization
  implicit val employeeWrites: Writes[Employee] = Json.writes[Employee]

  // Combine Reads and Writes into Format
  implicit val employeeFormat: Format[Employee] = Format(employeeReads, employeeWrites)
}
