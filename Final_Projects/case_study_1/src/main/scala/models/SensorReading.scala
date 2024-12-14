package models

import spray.json.DefaultJsonProtocol._
import spray.json._

import play.api.libs.json._
import spray.json._

case class SensorReading(
                          sensorId: Int,        // Unique ID of the sensor
                          timestamp: Long,      // Timestamp of the reading in milliseconds since epoch
                          temperature: Float,   // Temperature reading from the sensor
                          humidity: Float       // Humidity reading from the sensor
                        )

object JsonFormats {

  // Implicit format for SensorReading with RootJsonFormat for spray-json
  implicit val SensorReadingFormat: RootJsonFormat[SensorReading] = jsonFormat4(SensorReading)

  // Implicit Reads for SensorReading (for deserialization from JSON)
  implicit val SensorReadingReads: Reads[SensorReading] = Json.reads[SensorReading]

  // Implicit Writes for SensorReading (for serialization to JSON)
  implicit val SensorReadingWrites: Writes[SensorReading] = Json.writes[SensorReading]
}
