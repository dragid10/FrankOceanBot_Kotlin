import java.sql.*
import java.util.*

class MySQL(val dbProperties: Properties, var statement: Statement? = null, var conn: Connection? = null) {

    fun connectToMySQL() {
        //        Register JDBC Drive
        Class.forName(dbProperties.getProperty("jdbc_Driver"))

        try {
            //        Open connection to DB
            println("Attempting to connect to Database....".toUpperCase())
            conn = DriverManager.getConnection(dbProperties.getProperty("dbURL"), dbProperties)
        } catch (e: SQLException) {
            println("Error Connecting to MySQL. Retrying...")
            try {
                conn = DriverManager.getConnection(dbProperties.getProperty("dbURL"), dbProperties)
            } catch(e: Exception) {
                // TODO Add logging for if can't connect to DB
            }
        } finally {
            val resultMSG = if (conn != null) "Database Connection successful!" else "Database Connection Failed!"
            println(resultMSG)
        }
    }

    fun addLyricsToDB(lyric: String) {
//        Prepares statement for execution
        val query: PreparedStatement = conn!!.prepareStatement("INSERT INTO kotlinDB.lyrics_tbl(lyricLines) VALUES (?);")

//        Sets the variable to be added as the lyric Pair
        query.setString(1, lyric)
        query.executeUpdate()
    }

    fun getRandomLyric(): String? {
        var lyricToTweet: String? = null
        statement = conn?.createStatement()
        val result: ResultSet = statement!!.executeQuery("SELECT lyricLines FROM lyrics_tbl ORDER BY RAND() LIMIT 1")

        while (result.next()) {
            lyricToTweet = result.getString("lyricLines")
        }

        result.close()
        return lyricToTweet
    }

    fun closeConnection() {
        statement?.close()
        conn?.close()
    }
}