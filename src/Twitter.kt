import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.util.*

/**
 * Name: Alex Oladele
 * Unique-ID: OLADELAA
 * Date: 5/19/17
 * Project: FrankOceanBot_Kotlin
 */

class Twitter2(val properties: Properties) {
    fun connectToTwitter(): Twitter {
        val configBuilder = ConfigurationBuilder()

        println("Attempting to connect to Twitter....".toUpperCase())

        configBuilder.setDebugEnabled(properties.getProperty("debug").toBoolean())
        configBuilder.setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"))
        configBuilder.setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"))
        configBuilder.setOAuthAccessToken(properties.getProperty("oauth.accessToken"))
        configBuilder.setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"))

        val twitterFactory = TwitterFactory(configBuilder.build())

        val statusMsg = if (twitterFactory != null) "Twitter Connection Successful!" else "Could not connect to Twitter!"
        println(statusMsg)
        return twitterFactory.instance
    }

    fun tweetLyrics(lyrics: String, twitter: Twitter) {
        twitter.updateStatus(lyrics)
        println("Tweet Sent!")
    }
}