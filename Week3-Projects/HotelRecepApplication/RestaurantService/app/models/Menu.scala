package models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class Menu(id: Option[Int], foodItem: String, foodType: String, price: Double, date: LocalDate)


object Menu {
  implicit val format: OFormat[Menu] = Json.format[Menu]
}