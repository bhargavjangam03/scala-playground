trait GetStarted {
    def prepare(): Unit = {
        println("GetStarted: Preparation is underway.")
    }
}

trait Cook extends GetStarted {
    abstract override def prepare(): Unit = {
        super.prepare()  
        println("Cook: Cooking has been started.")
    }
}

trait Seasoning {
    def applySeasoning(): Unit = {
        println("Seasoning: Seasonal flavors have been added.")
    }
}

class Food extends Cook with Seasoning {
    def prepareFood(): Unit = {
        prepare()          
        applySeasoning()  
    }
}

object Run extends App {
    val food = new Food
    food.prepareFood()
}
