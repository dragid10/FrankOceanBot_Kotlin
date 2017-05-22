import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

/**
 * Name: Alex Oladele
 * Unique-ID: OLADELAA
 * Date: 5/19/17
 * Project: FrankOceanBot_Kotlin
 */

fun main(args: Array<String>) {
//    Username and Password for DB
    val dbInfo = File("DBUserName_Password.txt").readLines()

//    Create SQL object with username and password
    val mySQL = MySQL(dbInfo[0], dbInfo[1])

//    Create Twitter object to interact with Twitter
    val twitterObj = Twitter2()

//    Connects to the DB
//    mySQL.connectToMySQL()

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
    })

    timer.schedule(hourlyTask, 0L, ((1000 * 60 * 60) * 2))
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