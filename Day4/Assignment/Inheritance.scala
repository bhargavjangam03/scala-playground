// Main Object to Run Inheritance Examples
object Inheritance extends App {

  // Superclass Vehicle with Some Properties and Methods
  class Vehicle(val brand: String, val year: Int) {
    def start(): Unit = println(s"The $brand vehicle starts.")
    
    def info(): Unit = println(s"Vehicle Brand: $brand, Year: $year")
  }

  // 1. Single Inheritance - Car Class Inheriting Vehicle
  class Car(brand: String, year: Int, val model: String) extends Vehicle(brand, year) {
    def honk(): Unit = println(s"$brand $model honks!")
    
    // Overriding info to add more details
    override def info(): Unit = {
      super.info() // Call superclass info method
      println(s"Car Model: $model")
    }
  }

  // 2. Multilevel Inheritance - ElectricCar Extends Car
  class ElectricCar(brand: String, year: Int, model: String, val batteryLife: Int)
    extends Car(brand, year, model) {
    
    def charge(): Unit = println(s"$brand $model is charging.")
    
    // Overriding info to include battery details
    override def info(): Unit = {
      super.info() // Calls Car info which calls Vehicle info
      println(s"Battery Life: $batteryLife hours")
    }
  }

  // 3. Hierarchical Inheritance - Truck Class Inheriting Vehicle
  class Truck(brand: String, year: Int, val capacity: Int) extends Vehicle(brand, year) {
    def load(): Unit = println(s"The $brand truck loads with capacity $capacity tons.")
    
    // Overriding start to add specific behavior for Truck
    override def start(): Unit = println(s"The $brand truck starts with a roar!")
  }

  // Creating Instances and Demonstrating Inheritance and Overriding
  println("Inheritance Examples:")

  // Vehicle instance
  val vehicle = new Vehicle("Generic", 2020)
  vehicle.start()    // Output: The Generic vehicle starts.
  vehicle.info()     // Output: Vehicle Brand: Generic, Year: 2020

  // Car instance
  val car = new Car("Toyota", 2022, "Camry")
  car.start()        // Output: The Toyota vehicle starts.
  car.honk()         // Output: Toyota Camry honks!
  car.info()         // Output: Vehicle Brand: Toyota, Year: 2022, Car Model: Camry

  // ElectricCar instance (Multilevel Inheritance)
  val eCar = new ElectricCar("Tesla", 2023, "Model S", 400)
  eCar.start()       // Output: The Tesla vehicle starts.
  eCar.charge()      // Output: Tesla Model S is charging.
  eCar.info()        // Output: Vehicle Brand: Tesla, Year: 2023, Car Model: Model S, Battery Life: 400 hours

  // Truck instance (Hierarchical Inheritance)
  val truck = new Truck("Ford", 2019, 15)
  truck.start()      // Output: The Ford truck starts with a roar!
  truck.load()       // Output: The Ford truck loads with capacity 15 tons.
  truck.info()       // Output: Vehicle Brand: Ford, Year: 2019
}
