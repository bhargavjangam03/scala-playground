
class Person private (val name: String, val age: Int) {

  // Method to display person's information
  def display(): Unit = println(s"Person: Name = $name, Age = $age")
}

// Companion Object for the Person class
object Person {

  // Factory method to create a Person with validation
  def apply(name: String, age: Int): Person = {
    if (age >= 0) new Person(name, age)
    else {
      println("Invalid age. Age should be non-negative.")
      new Person(name, 0)
    }
  }

  // Static-like method to create a Person with default age
  def withDefaultAge(name: String): Person = new Person(name, 18)

  // Method to compare two persons by age
  def isOlder(person1: Person, person2: Person): Boolean = person1.age > person2.age
}

// Main object to run examples for Companion Class and Companion Object
object CompanionClass extends App {
  println("Companion Class and Companion Object Examples:")

  // Creating instances using the apply method in the companion object
  // In Scala, when you define a companion object with an apply method, 
  // you can create instances of the class without using the new keyword. This is a common pattern used to simplify object creation and implement factory methods.

  val person1 = Person("Alice", 30)
  val person2 = Person("Bob", -5)  // Will use default age as -5 is invalid
  val person3 = Person.withDefaultAge("Charlie")

  person1.display()  // Person: Name = Alice, Age = 30
  person2.display()  // Person: Name = Bob, Age = 0
  person3.display()  // Person: Name = Charlie, Age = 18

  // Comparing ages using companion object method
  println(s"Is ${person1.name} older than ${person3.name}? " + Person.isOlder(person1, person3)) // True or False
}
