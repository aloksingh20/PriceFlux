package com.example.priceflux.data.remote.scrapper

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.inject.Inject

class ScrapperExecuter {

    fun connectWithRetry(url: String): Document {

        var attempts = 0
        var doc: Document? = null
        while (attempts < 3) { // Retry up to 3 times
            try {
                val connection: Connection = Jsoup.connect(url).userAgent(USER_AGENT)
                doc = connection.get()
                break
            } catch (e: IOException) {
                attempts++
                if (attempts == 3) {
                    throw e // If all retries fail, propagate the exception
                }
                // Sleep for a short time before retrying
                Thread.sleep(3000)
            }
        }
        return doc!!
    }
    companion object{
        val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
    }
}