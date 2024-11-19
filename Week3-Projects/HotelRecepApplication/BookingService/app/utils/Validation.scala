package utils

import scala.util.matching.Regex

object Validation {
  private val emailRegex: Regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".r
  private val phoneRegex: Regex = "^[0-9]{10}$".r

  def validateEmail(email: String): Boolean = emailRegex.matches(email)

  def validatePhoneNumber(phoneNumber: String): Boolean = phoneRegex.matches(phoneNumber)
}
