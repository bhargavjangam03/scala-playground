def multipart(name:String)(logic:Unit): Unit = {
      println(s"Hi $name")
      logic
      
    
}

@main def funccall() = {
multipart("Bhargav"){
   println("I am going to do some post processing here")
}
}

// note : Here logic is not represented by lambda so
// here logic is unit,it's not a method, so "I am going to do some post processing here" prints first and then "Hi Bhargav"
// If you try to print logic here ,it will print (),because logic is unit which is basically void

// Block executes on spot 