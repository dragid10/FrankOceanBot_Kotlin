import java.sql.*
import java.util.*

class MySQL(val username: String, val password: String, var statement: Statement? = null, var conn: Connection? = null, val jdbc_driver: String = "com.mysql.jdbc.Driver", val db_url: String = "jdbc:mysql://kotlindb.ddns.net:3305/kotlinDB?useSSL=false") {

    fun connectToMySQL(username: String = this.username, password: String = this.password) {
//        Register JDBC Drive
        Class.forName(jdbc_driver)

        val properties = Properties()
        properties.setProperty("user", username)
        properties.setProperty("password", password)
        properties.setProperty("userSSL", "false")
        properties.setProperty("autoReconnect", "true")

//        Open connection to DB
        println("Connecting to Database...")
        conn = DriverManager.getConnection(db_url, properties)
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
        return lyricToTweet
    }
}