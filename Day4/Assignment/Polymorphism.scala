object Polymorphism extends App {

  // Overloading Examples in Calculator Class
  class Calculator {

    // 1. Overloading by Number of Parameters
    def add(a: Int): Int = a

    def add(a: Int, b: Int): Int = a + b

    def add(a: Int, b: Int, c: Int): Int = a + b + c

    // 2. Overloading by Parameter Types
    def multiply(a: Int, b: Int): Int = a * b

    def multiply(a: Double, b: Double): Double = a * b

    // 3. Overloading by Order of Parameters
    def format(value: Int, prefix: String): String = s"$prefix$value"

    def format(prefix: String, value: Int): String = s"$prefix$value"

    // 4. Overloading with Default Parameters
    def greet(name: String = "Guest"): String = s"Hello, $name!"

    def greet(name: String, age: Int): String = s"Hello, $name! You are $age years old."

    // 5. Overloading with Variable-Length Arguments (Varargs)
    def sum(values: Int*): Int = values.sum

    // 6. Simulated Overloading by Return Type Difference (Using Parameter Types)
    def print(value: Int): Unit = println(s"Int: $value")

    def print(value: String): Unit = println(s"String: $value")
  }

  // Demonstrate Calculator Overloading Examples
  val calculator = new Calculator()
  println("Calculator Overloading Examples:")
  println(calculator.add(5))                   // Output: 5
  println(calculator.add(5, 10))               // Output: 15
  println(calculator.add(5, 10, 15))           // Output: 30
  println(calculator.multiply(3, 4))           // Output: 12
  println(calculator.multiply(2.5, 4.5))       // Output: 11.25
  println(calculator.format(123, "Value: "))   // Output: Value: 123
  println(calculator.format("Value: ", 456))   // Output: Value: 456
  println(calculator.greet())                  // Output: Hello, Guest!
  println(calculator.greet("Alice", 25))       // Output: Hello, Alice! You are 25 years old.
  println(calculator.sum(1, 2, 3, 4, 5))       // Output: 15
  calculator.print(10)                         // Output: Int: 10
  calculator.print("Scala")                    // Output: String: Scala

  // Overriding Examples with Animal Class
  class Animal {
    def sound(): String = "Some sound"
  }

  class Dog extends Animal {
    override def sound(): String = "Bark"
  }

  class Cat extends Animal {
    override def sound(): String = "Meow"
  }

  println("\nAnimal Sound Overriding Examples:")
  val animals: List[Animal] = List(new Dog, new Cat)
  animals.foreach(animal => println(animal.sound()))  // Output: Bark, Meow

  // Overriding Examples with Shape Class
  class Shape {
    def area(): Double = 0.0
  }

  class Circle(val radius: Double) extends Shape {
    override def area(): Double = Math.PI * radius * radius
  }

  class Rectangle(val length: Double, val width: Double) extends Shape {
    override def area(): Double = length * width
  }

  println("\nShape Area Overriding Examples:")
  val circle = new Circle(5)
  val rectangle = new Rectangle(4, 6)
  println(s"Circle area: ${circle.area()}")         // Output: Circle area: 78.53981633974483
  println(s"Rectangle area: ${rectangle.area()}")   // Output: Rectangle area: 24.0

  // Constructor Overloading Example
  class Employee(val name: String, val age: Int) {
    var salary: Double = 0.0

    // Secondary constructor
    def this(name: String, age: Int, salary: Double) = {
      this(name, age)
      this.salary = salary
    }

    def displayDetails(): Unit = {
      println(s"Employee Name: $name, Age: $age, Salary: $$${salary}")
    }
  }

  println("\nConstructor Overloading Example:")
  val emp1 = new Employee("John", 30)
  val emp2 = new Employee("Jane", 28, 50000.0)
  emp1.displayDetails()  // Output: Employee Name: John, Age: 30, Salary: $0.0
  emp2.displayDetails()  // Output: Employee Name: Jane, Age: 28, Salary: $50000.0

  // Operator Overloading Example
  class Complex(val real: Int, val imaginary: Int) {

    // Overloading + operator for Complex numbers
    def +(that: Complex): Complex = {
      new Complex(this.real + that.real, this.imaginary + that.imaginary)
    }

    // Overloading * operator for Complex numbers
    def *(that: Complex): Complex = {
      new Complex(
        this.real * that.real - this.imaginary * that.imaginary,
        this.real * that.imaginary + this.imaginary * that.real
      )
    }

    override def toString: String = s"$real + ${imaginary}i"
  }

  println("\nOperator Overloading Example:")
  val complex1 = new Complex(2, 3)
  val complex2 = new Complex(4, 5)
  val sum = complex1 + complex2
  val product = complex1 * complex2
  println(s"Sum of complex numbers: $sum")         // Output: Sum of complex numbers: 6 + 8i
  println(s"Product of complex numbers: $product") // Output: Product of complex numbers: -7 + 22i
}
