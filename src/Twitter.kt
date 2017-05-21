import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.io.File

/**
 * Name: Alex Oladele
 * Unique-ID: OLADELAA
 * Date: 5/19/17
 * Project: FrankOceanBot_Kotlin
 */

class Twitter2 {
    fun connectToTwitter(): Twitter {
        val configBuilder = ConfigurationBuilder()
        val configFile = File("twitter4j.properties").readLines()
        configBuilder.setDebugEnabled(configFile[0].toBoolean())
        configBuilder.setOAuthConsumerKey(configFile[1])
        configBuilder.setOAuthConsumerSecret(configFile[2])
        configBuilder.setOAuthAccessToken(configFile[3])
        configBuilder.setOAuthAccessTokenSecret(configFile[4])

        val twitterFactory = TwitterFactory(configBuilder.build())
        return twitterFactory.instance
    }

    fun tweetLyrics(lyrics: String, twitter: Twitter): Status = twitter.updateStatus(lyrics)
}