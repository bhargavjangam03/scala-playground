// Question 2
// ----------

// trait Task --->  doTask (implemented method)
// trait Cook  extends Task --> doTask (override)
// trait Garnish extends Cook --> doTask (overide)
// trait Pack extends Garnish --> doTask (overide)
// class Activity extends Task---> doActivity ---> Call for doTask


// create object in main method 

// val:Task = new Activity with Cook with Garnish with Pack

// observe the behavior

// observe the behavior by changing the order of inheritance


trait Task {
    def doTask(task: String): Unit = {
        println(s"Doing task $task")
    }
}

trait Cook extends Task {
    override def doTask(task: String): Unit = {
        super.doTask(task)
        println("I am cooking")
    }
}

trait Garnish extends Cook {
    override def doTask(task: String): Unit = {
        super.doTask(task)
        println("I am garnishing")
    }
}

trait Pack extends Garnish {
    override def doTask(task: String): Unit = {
        super.doTask(task)
        println("I am packing garnished food")
    }
}

class Activity extends Task {
    def doActivity(activity: String): Unit= {
        doTask(activity)
        println(s"I am busy with $activity")
    }
}

object TraitCombinationTest extends App {
    println("Combination 1: Cook -> Garnish -> Pack")
    val task1: Task = new Activity with Cook with Garnish with Pack
    task1.doTask("preparing the meal")
    println("-----")

    println("Combination 2: Pack -> Garnish -> Cook")
    val task2: Activity = new Activity with Pack with Garnish with Cook
    task2.doActivity("preparing the meal")
    println("-----")

    println("Combination 3: Garnish -> Cook -> Pack")
    val task3: Task = new Activity with Garnish with Cook with Pack
    task3.doTask("preparing the meal")
    println("-----")

    println("Combination 4: Garnish -> Pack -> Cook")
    val task4: Activity = new Activity with Garnish with Pack with Cook
    task4.doActivity("preparing the meal")
    println("-----")

    println("Combination 5: Pack -> Cook")
    val task5: Task = new Activity with Pack with Cook
    task5.doTask("preparing the meal")
    println("-----")

    println("Combination 6: Cook -> Pack")
    val task6: Activity = new Activity with Cook with Pack
    task6.doActivity("preparing the meal")
    println("-----")

    println("Combination 7: Only Pack")
    val task7: Task = new Activity with Pack
    task7.doTask("preparing the meal")
    println("-----")

    println("Combination 8: Only Cook")
    val task8: Activity = new Activity with Cook
    task8.doActivity("preparing the meal")
    println("-----")
}
