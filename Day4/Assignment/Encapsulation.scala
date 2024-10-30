class Employee(val id: Int, // Immutable and accessible outside
               var name: String, // Mutable and accessible outside
               initialSalary: Double, // Private to the constructor, accessible within class only
               private var age: Int // Mutable within class, but not accessible outside directly
              ) {

  // Secondary Constructor 1: Provides default age
  def this(id: Int, name: String, salary: Double) = {
    this(id, name, salary, 25) // Default age
  }

  // Secondary Constructor 2: Provides default age and salary
  def this(id: Int, name: String) = {
    this(id, name, 50000.0, 25) // Default salary and age
  }

  // Private variable to hold salary, allowing internal modification
  private var salary: Double = initialSalary

  // Getter for salary
  def getSalary: Double = salary

  // Setter for salary
  def setSalary(newSalary: Double): Unit = {
    if (newSalary >= 0) salary = newSalary else println("Salary cannot be negative.")
  }


  // Getter for age (since it is private, exposing it through a method)
  def getAge: Int = age

   
  // we cannot set id,since it has val annotation which makes it immutable

  // name can be set directly from outside since it is not private

  // salary is accessed internally but not for outside
  //def getSalary: Double = initialSalary 
  // This will fail - salary is not a field

  //def setSalary(newSalary: Double): Unit = {
    // Attempting to assign a new value to `salary` will also fail
   // initialSalary = newSalary // This will also fail
  //}

  // Setter for age (since it is private, allowing modification through a method)
  def setAge(newAge: Int): Unit = {
    if (newAge > 0) age = newAge else println("Age must be positive.")
  }

  // Method to display employee details
  def displayDetails(): Unit = {
    println(s"ID: $id, Name: $name, Age: $age, Salary: $$${salary}")
  }

  // Example of modifying a mutable field
  def giveRaise(amount: Double): Unit = {
    if (amount > 0) {
      // Note: Cannot directly modify `salary` since it isn't a var; you'd need a separate method or mutable var here
      println(s"Salary updated with raise of $$amount")
    } else {
      println("Raise amount must be positive.")
    }
  }
}

object EmployeeApp extends App {
  // Creating instances using different constructors
  val emp1 = new Employee(1, "Alice", 60000.0, 30) // Using primary constructor
  val emp2 = new Employee(2, "Bob", 55000.0) // Using secondary constructor with default age
  val emp3 = new Employee(3, "Charlie") // Using secondary constructor with default salary and age

  // Accessing and modifying properties
  println(s"Employee ID: ${emp1.id}")       // Accessible since id is a `val`
  println(s"Employee Name: ${emp1.name}")   // Accessible and mutable since name is a `var`

  emp1.name = "Alice Smith" // Modifying name
  println(s"Updated Name: ${emp1.name}")

  // Accessing salary directly would cause an error:
  // println(emp1.salary) // This line would cause an error because salary is not accessible outside

  // Accessing age via getter since age is private
  println(s"Employee Age: ${emp1.getAge}")
  emp1.setAge(31) // Modifying age via setter

  // Displaying details
  emp1.displayDetails() // Outputs all the details

  // Creating an employee with default parameters
  emp3.displayDetails() // Shows default values for age and salary
}
