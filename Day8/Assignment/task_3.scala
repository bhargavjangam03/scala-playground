import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement}


case class Candidate(sno: Int, name: String, city: String)

val candidateData: Array[(Int, String, String)] = Array(
  (1, "Alice", "New York"), (2, "Bob", "Los Angeles"), (3, "Charlie", "Chicago"),
  (4, "Diana", "Houston"), (5, "Eve", "Phoenix"), (6, "Frank", "Philadelphia"),
  (7, "Grace", "San Antonio"), (8, "Hank", "San Diego"), (9, "Ivy", "Dallas"),
  (10, "Jack", "San Jose"), (11, "Kathy", "Austin"), (12, "Leo", "Jacksonville"),
  (13, "Mona", "Fort Worth"), (14, "Nina", "Columbus"), (15, "Oscar", "Charlotte"),
  (16, "Paul", "San Francisco"), (17, "Quinn", "Indianapolis"), (18, "Rita", "Seattle"),
  (19, "Steve", "Denver"), (20, "Tina", "Washington"), (21, "Uma", "Boston"),
  (22, "Vince", "El Paso"), (23, "Wendy", "Detroit"), (24, "Xander", "Nashville"),
  (25, "Yara", "Portland"), (26, "Zane", "Oklahoma City"), (27, "Aiden", "Las Vegas"),
  (28, "Bella", "Louisville"), (29, "Caleb", "Baltimore"), (30, "Daisy", "Milwaukee"),
  (31, "Ethan", "Albuquerque"), (32, "Fiona", "Tucson"), (33, "George", "Fresno"),
  (34, "Hazel", "Mesa"), (35, "Ian", "Sacramento"), (36, "Jill", "Atlanta"),
  (37, "Kyle", "Kansas City"), (38, "Luna", "Colorado Springs"), (39, "Mason", "Miami"),
  (40, "Nora", "Raleigh"), (41, "Owen", "Omaha"), (42, "Piper", "Long Beach"),
  (43, "Quincy", "Virginia Beach"), (44, "Ruby", "Oakland"), (45, "Sam", "Minneapolis"),
  (46, "Tara", "Tulsa"), (47, "Ursula", "Arlington"), (48, "Victor", "New Orleans"),
  (49, "Wade", "Wichita"), (50, "Xena", "Cleveland")
)

// Implicit conversion from Tuple to Candidate case class
implicit def tupleToCandidate(row: (Int, String, String)): Candidate = {
  Candidate(row._1, row._2, row._3)
}

// Method to get a connection to the database
def getConnection(): Connection = {
  val url = "jdbc:mysql://scaladb.mysql.database.azure.com:3306/bhargav"
  val username = "mysqladmin"
  val password = "Password@12345"
  DriverManager.getConnection(url, username, password)
}

// Method to insert a Candidate record into the candidates table
def insertMethod(candidate: Candidate, statement: PreparedStatement): Unit = {
  statement.setInt(1, candidate.sno)
  statement.setString(2, candidate.name)
  statement.setString(3, candidate.city)
  statement.executeUpdate()
}


object Database {
  def main(args: Array[String]): Unit = {

    Class.forName("com.mysql.cj.jdbc.Driver")

    val connection: Connection = getConnection()

    var statement: Statement = null
    var preparedStatement: PreparedStatement = null
    var resultSet: ResultSet = null

    try {

        statement = connection.createStatement()
        val createTableSQL =
            """
            CREATE TABLE IF NOT EXISTS candidates (
                sno INT PRIMARY KEY,
                name VARCHAR(50),
                city VARCHAR(50)
            );
            """
        statement.execute(createTableSQL)
        println("Table created successfully.")


        val insertSQL = "INSERT INTO candidates (sno, name, city) VALUES (?, ?, ?)"
        preparedStatement = connection.prepareStatement(insertSQL)

        candidateData.foreach { data =>
            insertMethod(data, preparedStatement)
        }
        println("Data inserted successfully.")

        // Step 3: Query the table to verify data insertion
        statement = connection.createStatement()
        val query = "SELECT * FROM candidates"
        resultSet = statement.executeQuery(query)

        // Process and display the ResultSet
        println("Candidates:")
        while (resultSet.next()) {
            val sno = resultSet.getInt("sno")
            val name = resultSet.getString("name")
            val city = resultSet.getString("city")
            println(s"Sno: $sno, Name: $name, City: $city")
        }

    } catch {
        case e: Exception => e.printStackTrace()
    } finally {
        if (resultSet != null) resultSet.close()
        if (statement != null) statement.close()
        if (preparedStatement != null) preparedStatement.close()
        if (connection != null) connection.close()
    }
  }
}
