package com.example.priceflux.data.remote.scrapper

import android.util.Log
import com.example.priceflux.data.remote.amazon.AmazonRemoteApi
import com.example.priceflux.data.remote.RemoteDto
import com.example.priceflux.util.Objects
import com.example.priceflux.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmazonScrapper @Inject constructor(private val scrapperExecuter: ScrapperExecuter):AmazonRemoteApi {
    override suspend fun getSearchProducts(search: String): Resource<List<RemoteDto>> {
        val searchUrl = "${Objects.AMAZON_URL}/s?k=$search"
        return try {
                 val doc = scrapperExecuter.connectWithRetry(searchUrl)
                 val products = doc.select(".s-result-item")
                Log.d("products",products.toString())
                 val productsList = mutableListOf<RemoteDto>()
                    for(productElement in products){
                        val productNameElement = productElement.select("div[data-cy=title-recipe] span.a-text-normal")
                        val productName = productNameElement.text() ?: "Product name not found"

                        val priceElement = productElement.select("span.a-offscreen")
                        val productPrice = priceElement.text() ?: "Price not available"
                        val productUrl = productNameElement.select("a.a-link-normal.s-no-outline").attr("href")
                        val imgElement = productElement.select("img").first()

                        // Get the value of the "src" attribute of the <img> tag
                        val productImage = imgElement?.absUrl("src")?: ""
                        val productRating = productElement.select("i.a-icon.a-icon-star-small.a-star-small-4-5.aok-align-bottom > span.a-icon-alt").first()

                        if(productName.isNotEmpty() && productPrice.isNotEmpty()) {
                            productsList.add(
                                RemoteDto(
                                    productName,
                                    productPrice,
                                    productImage,
                                    productUrl,
                                    "",
                                    productRating?.text()?:""

                                )
                            )
                        }
                    }
//            Log.d("amazon_products",productsList.toString())
                 Resource.Success(productsList)
             } catch (e: Exception) {

                 Resource.Error("Could not load products")
             }

    }

    override suspend fun getProductsDetails(searchQuery: String): Resource<RemoteDto>{
        val searchUrl = "${Objects.AMAZON_URL}s?k=$searchQuery"
        return try {
            val response = scrapperExecuter.connectWithRetry(searchUrl)
            val productNameElement = response.select("div[data-cy=title-recipe] span.a-text-normal")
            val productName = productNameElement.text()
            val productPriceElement = response.select(".a-price .a-offscreen")
            val productPrice = productPriceElement.text()

            val productRatingElement = response.select(".a-icon-alt")
            val productRating = productRatingElement.text()
            val productDescriptionElement = response.select(".a-expander-content")
            val productDescription = productDescriptionElement.text()

            Resource.Success(RemoteDto(productName,productPrice,productRating,productDescription,"",""))
        } catch (e: Exception) {

            Resource.Error("Could not load products info")
        }
    }

}