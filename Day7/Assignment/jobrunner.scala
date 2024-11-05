
def jobRunner(name:String,time:Int)(logic: String=>Unit): Unit = {
      println(s"Hi! $name")
      Thread.sleep(time*1000)
      logic(name)
}

@main def funccall() = {
jobRunner("Bhargav",4){ name =>
   println(s"Bye! $name")
}
}
