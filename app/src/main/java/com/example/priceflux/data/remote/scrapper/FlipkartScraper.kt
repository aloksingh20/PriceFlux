package com.example.priceflux.data.remote.scrapper

import android.util.Log
import com.example.priceflux.data.remote.amazon.RemoteDto
import com.example.priceflux.data.remote.flipkart.FlipkartRemoteApi
import com.example.priceflux.util.Objects
import com.example.priceflux.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlipkartScraper@Inject constructor(private val scrapperExecuter: ScrapperExecuter):FlipkartRemoteApi {
    override suspend fun getSearchProducts(searchQuery: String): Resource<List<RemoteDto>> {
        val searchUrl = "${Objects.FLIPKART_URL}/search?q=${searchQuery.replace(" ", "+")}"
        return try {
            val doc = scrapperExecuter.connectWithRetry(searchUrl)
            val products = doc.select("div._75nlfW")

            val productsList = mutableListOf<RemoteDto>()
            for (productElement in products) {

                    val productUrl = when {
                        searchQuery.matches(Regex("[0-9]+")) -> productElement.select("a.VJA3rP").attr("href") // Barcode search
                        else -> productElement.select("a.CGtC98, a.VJA3rP, a,WKTcLC").attr("href") // Product name search
                    }
                    val productName = when {
                        searchQuery.matches(Regex("[0-9]+")) -> productElement.select("a.wjcEIp").attr("title") // Barcode search
                        else -> productElement.select("div.KzDlHZ ,a.wjcEIp, div.syl9yP").text() // Product name search
                    }
                    val productPrice = productElement.select(".Nx9bqj").text()
                    val productImage = productElement.select("img.DByuf4, img._53J4C-").attr("src")
                    val prodDescription = productElement.select("a.WKTcLC, ul.G4BRas > li").text()

                val productRating = productElement.select("div.XQDdHH").text()
                productsList.add(RemoteDto(productName, productPrice,productImage,productUrl,prodDescription,productRating))
            }
            Log.d("FlipkartScraper", productsList.toString())
            Resource.Success(productsList)
        } catch (e: Exception) {
            Log.d("FlipkartScraper", "Could not load products")
            Resource.Error("Could not load products")
        }

    }

    override suspend fun getProductsDetails(searchQuery: String): Resource<RemoteDto> {
        val searchUrl = "${Objects.FLIPKART_URL}/search?q=${searchQuery.replace(" ", "+")}"

        return try {
            val doc = scrapperExecuter.connectWithRetry(searchUrl)

            val productElement = doc.select("div._75nlfW, div._1AtVbE") // CSS selectors for both cases

                val productUrl = when {
                    searchQuery.matches(Regex("[0-9]+")) -> productElement.select("a.VJA3rP")
                        .attr("href") // Barcode search
                    else -> productElement.select("a._2rpwqI")
                        .attr("href") // Product name search
                }
                val productName = when {
                    searchQuery.matches(Regex("[0-9]+")) -> productElement.select("a.wjcEIp")
                        .text() // Barcode search
                    else -> productElement.select("div._4rR01T, div.syl9yP").text() // Product name search
                }
                val productPrice = productElement.select("div._30jeq3").text()

            Resource.Success(RemoteDto(productName, productPrice, productName, productUrl,"",""))

        } catch (e: Exception) {

            Resource.Error("Could not load products info")
        }

    }

}