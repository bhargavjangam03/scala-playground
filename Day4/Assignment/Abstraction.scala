// Abstract class Employee with access specifiers
abstract class Employee(private val id: Int, protected var name: String, private var age: Int) {

  // Public method (no access modifier needed) - available to anyone who has access to an Employee object
  def calculateSalary(): Double

  // Protected method - accessible within this class and its subclasses
  protected def getName: String = name

  // Private method - accessible only within the Employee class
  private def incrementAge(): Unit = {
    age += 1
    println(s"Age incremented internally. New Age: $age")
  }

  // Public method to display employee details, calls private method incrementAge within the class
  def displayDetails(): Unit = {
    incrementAge() // Private method can be called within the class
    println(s"Employee ID: $id, Name: ${getName}, Age: $age, Salary: $${calculateSalary()}")
  }
}

// Full-time employee subclass
class FullTimeEmployee(id: Int, name: String, age: Int, private var baseSalary: Double) extends Employee(id, name, age) {

  // Implementation of calculateSalary for full-time employees
  override def calculateSalary(): Double = baseSalary

  // Accesses protected member from superclass
  def displayFullName(): Unit = {
    println(s"Full Name of Employee: ${getName}") // `getName` is protected, so accessible here
  }

  // Method to give a raise, which updates baseSalary
  def giveRaise(amount: Double): Unit = {
    if (amount > 0) {
      baseSalary += amount
      println(s"Salary increased by $$${amount}. New Salary: $$${baseSalary}")
    } else {
      println("Raise amount must be positive.")
    }
  }
}

// Part-time employee subclass
class PartTimeEmployee(id: Int, name: String, age: Int, private var hourlyRate: Double, private val hoursWorked: Int) 
  extends Employee(id, name, age) {
  
  // Implementation of calculateSalary for part-time employees
  override def calculateSalary(): Double = hourlyRate * hoursWorked

  // Method to update hourly rate
  def setHourlyRate(newRate: Double): Unit = {
    if (newRate >= 0) hourlyRate = newRate else println("Hourly rate cannot be negative.")
  }

  // Cannot access private member `incrementAge` from superclass
  def attemptIncrementAge(): Unit = {
    // incrementAge() // This would cause a compilation error because incrementAge is private to Employee
  }
}

object EmployeeApp extends App {
  // Creating instances of FullTimeEmployee and PartTimeEmployee
  val fullTimeEmp = new FullTimeEmployee(1, "Alice", 30, 60000)
  val partTimeEmp = new PartTimeEmployee(2, "Bob", 25, 25, 160)

  // Displaying details of each employee
  fullTimeEmp.displayDetails() // Employee ID: 1, Name: Alice, Age: 31, Salary: $60000.0
  partTimeEmp.displayDetails() // Employee ID: 2, Name: Bob, Age: 26, Salary: $4000.0

  // Accessing public methods
  fullTimeEmp.giveRaise(5000)
  partTimeEmp.setHourlyRate(30)

  // Accessing protected member via subclass method
  fullTimeEmp.displayFullName()

  // Trying to access private members - this will cause a compilation error if uncommented
  // println(fullTimeEmp.age)        // Error: age is private
  // fullTimeEmp.incrementAge()      // Error: incrementAge is private
}
