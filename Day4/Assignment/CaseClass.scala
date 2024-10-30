// CaseClass.scala

// Case Class for an Employee with properties and default implementations for equals, hashCode, and toString
case class Employee(name: String, position: String, salary: Double) {

  // Method to give a raise to the employee
  def giveRaise(amount: Double): Employee = {
    if (amount > 0) this.copy(salary = this.salary + amount) // Case classes have a copy method
    else {
      println("Raise amount should be positive.")
      this
    }
  }
}

// Main object to run examples for the Case Class
object CaseClass extends App {
  println("\nCase Class Examples:")

  // Creating case class instances
  val employee1 = Employee("Dave", "Software Engineer", 75000)
  val employee2 = Employee("Emma", "Manager", 85000)

  // Printing details (uses toString by default)
  println(employee1)  // Employee(Dave, Software Engineer, 75000.0)
  println(employee2)  // Employee(Emma, Manager, 85000.0)

  // Giving a raise to employee1
  val updatedEmployee1 = employee1.giveRaise(5000)
  println(s"After raise: $updatedEmployee1")  // Employee(Dave, Software Engineer, 80000.0)

  // Checking equality between two Employee instances
  val employee3 = Employee("Dave", "Software Engineer", 80000)
  println("Is updatedEmployee1 equal to employee3? " + (updatedEmployee1 == employee3)) // true

  // Copying case class with modification
  val employee4 = employee1.copy(position = "Senior Software Engineer")
  println(s"Employee with updated position: $employee4") // Employee(Dave, Senior Software Engineer, 75000.0)
}
