import scala.io.StdIn

class Employee(var serialNo: Int, var name: String, var city: String){
    override def toString: String = s"($serialNo, $name, $city)"
}

class TreeNode(var name: String){
    var employees: List[Employee] = List()
    var children: List[TreeNode] = List()
}



class Organization(){

    private val root: TreeNode = new TreeNode("Apple")

    def addEmployees(parentDept: String, actualDept: String, employees: List[Employee]): Unit = {
        val actualDeptNode = new TreeNode(actualDept)
        
        if (parentDept.isEmpty){
            addChild(root,actualDeptNode)
        }
        else{
            findNodeDFS(root, parentDept) match{
                case Some(parentNode) =>
                if (parentNode.children.exists(_.name == actualDept)){
                    parentNode.children.find(_.name == actualDept).foreach(_.employees++=employees)
                }
                else{
                    addChild(parentNode, actualDeptNode)
                    actualDeptNode.employees++= employees
                }
                case None=>
                println(s"Parent department '$parentDept' not found.")
            }
        }
    }

    private def addChild(parent: TreeNode, child: TreeNode): Unit = {
        parent.children = child :: parent.children
    }

    private def findNodeDFS(node: TreeNode, department: String): Option[TreeNode] = {
        if (node.name == department) {
        Some(node)
        } else {
        node.children.toStream // Use a Stream to handle potentially large trees lazily
            .flatMap(child => findNodeDFS(child, department)) // Find in child nodes
            .headOption // Get the first matching node if any
        }
    }

    def printHierarchy(): Unit = {
        println("Hierarchy:")
        println()
        printTree(root,"")
      }
    
    private def printTree(node: TreeNode, prefix: String): Unit = {
        println(s"$prefix${node.name}")
        for (emp<-node.employees){
            println(s"$prefix    $emp")
        }
        for (child <- node.children) {
        printTree(child, s"$prefix    ")
        }
    }
}

object org extends App{

    val organization = new Organization()

    
    while(true){
        println("Choose an option:")
        println("1. Add a list of Employees")
        println("2. Print organization hierarchy")
        println("3. Exit")
        val choice = StdIn.readLine()

        choice match{
            case "1" => addEmployeesToOrg()
            case "2" => organization.printHierarchy()
            case "3" => 
                println("Exiting....")
                System.exit(0)
            case _ => println("Invalid option. Please try again.")
        }
    }
        
    def addEmployeesToOrg(): Unit = {
        println("Enter parent department name:")
        val parentDept = StdIn.readLine()

        println("Enter actual department name:")
        val actualDept = StdIn.readLine()

        println("Enter employee details (serialno,name,city) separated by commas (leave blank if no employees):")
        val empInput = StdIn.readLine().trim

        val employees = if(empInput.isEmpty) List.empty[Employee]
                        else empInput.split(";").toList.map{ empStr =>
                            val Array(serialNo,name,city) = empStr.split(",").map(_.trim)
                            new Employee(serialNo.toInt, name, city)
                        }
        organization.addEmployees(parentDept, actualDept, employees)

    }   
}
