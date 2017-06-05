import java.sql.*
import java.util.*

class MySQL(val username: String, val password: String, var statement: Statement? = null, var conn: Connection? = null) {

    fun connectToMySQL(username: String = this.username, password: String = this.password) {
        val dbProperties = Properties()
        dbProperties.setProperty("user", username)
        dbProperties.setProperty("password", password)
        dbProperties.setProperty("userSSL", "false")
        dbProperties.setProperty("autoReconnect", "true")
        dbProperties.setProperty("jdbc_Driver", "com.mysql.jdbc.Driver")
        dbProperties.setProperty("dbURL", "jdbc:mysql://kotlindb.ddns.net:3305/kotlinDB?useSSL=false")

        //        Register JDBC Drive
        Class.forName(dbProperties.getProperty("jdbc_Driver"))

//        Open connection to DB
        println("Connecting to Database...")
        conn = DriverManager.getConnection(dbProperties.getProperty("dbURL"), dbProperties)
        println("Database Connection successful!")
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