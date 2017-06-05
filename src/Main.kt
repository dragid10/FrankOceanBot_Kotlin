import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Name: Alex Oladele
 * Unique-ID: OLADELAA
 * Date: 5/19/17
 * Project: FrankOceanBot_Kotlin
 */

fun main(args: Array<String>) {

    val prop: Properties = Properties()
    val input: InputStream

    try {
//    Load the Properties from the config file
        input = FileInputStream("config.prop")
        prop.load(input)
        input.close()
    } catch (e: Exception) {
        e.printStackTrace()
        // TODO Add fallback actions
    } finally {
//        Create the property objects for the different classes
        val dbProperties = createDBProperties(prop)
        val twitterProperties = createTwitterProperties(prop)

        //    Create SQL object with username and password
        val mySQL = MySQL(dbProperties)

//    Create Twitter object to interact with Twitter
        val twitterObj = Twitter2(twitterProperties)

//    Connects to Twitter
        val twitterConn = twitterObj.connectToTwitter()

        //    Adds lyrics to DB
//    val lyricsFiles = File("lyrics").listFiles()
//    addDirectoryToDB(mySQL, *lyricsFiles)

        val timer = Timer()
        val hourlyTask = timerTask({
            mySQL.connectToMySQL()
            twitterObj.tweetLyrics(mySQL.getRandomLyric()!!, twitterConn)
            mySQL.closeConnection()
            println("Database Connection successfully closed")
        })
        timer.schedule(hourlyTask, 0L, ((1000 * 60 * 60) * 2))
    }
}

fun createTwitterProperties(prop: Properties): Properties {
    val twitterProp = Properties()
    twitterProp.setProperty("debug", prop.getProperty("debug"))
    twitterProp.setProperty("oauth.consumerKey", prop.getProperty("oauth.consumerKey"))
    twitterProp.setProperty("oauth.consumerSecret", prop.getProperty("oauth.consumerSecret"))
    twitterProp.setProperty("oauth.accessToken", prop.getProperty("oauth.accessToken"))
    twitterProp.setProperty("oauth.accessTokenSecret", prop.getProperty("oauth.accessTokenSecret"))

    return twitterProp
}

fun createDBProperties(prop: Properties): Properties {
    val dbProp = Properties()
    dbProp.setProperty("user", prop.getProperty("db_user"))
    dbProp.setProperty("password", prop.getProperty("db_pass"))
    dbProp.setProperty("autoReconnect", prop.getProperty("autoReconnect"))
    dbProp.setProperty("userSSL", "false")
    dbProp.setProperty("jdbc_Driver", prop.getProperty("jdbc_Driver"))
    dbProp.setProperty("dbURL", prop.getProperty("dbURL"))

    return dbProp
}

fun addDirectoryToDB(mySQL: MySQL, vararg songList: File) {
//    Parses through directory to find songs and add them to DB
    if (songList[0].isDirectory) {
        val innerDir = songList.toList()
        innerDir
                .map { it.listFiles() }
                .forEach {
                    for (file in it) {
                        addSongToDB(file, mySQL)
                    }
                }
    } else {
        for (song in songList) {
            addSongToDB(song, mySQL)
        }
    }
}

fun addSongToDB(song: File, mySQLContext: MySQL) {
    val songLyrics = song.readLines()
    var line1: String? = null
    var line2: String? = null
    val lyricPairs = mutableListOf<String>()

//    Takes the lyrics and pairs them up to be added to DB
    for (lyric in songLyrics) {
        if (line1 != null && line2 != null) {
            lyricPairs.add(line1.capitalize() + '\n' + line2.capitalize())
            line1 = null
            line2 = null
        }


        if (line1 == null) {
            line1 = lyric.trim()
        } else if (line2 == null) {
            line2 = lyric.trim()
        }
    }

//    Catch the last line in the Song
    if (line1 == null) line1 = ""
    if (line2 == null) line2 = ""
    lyricPairs.add(line1.capitalize() + '\n' + line2.capitalize())

//    Add each lyric in lyricPairs to the database
    lyricPairs
            .filter { it.isNotBlank() }
            .forEach { mySQLContext.addLyricsToDB(it) }
    println("Lyrics Added to Database!")
}

