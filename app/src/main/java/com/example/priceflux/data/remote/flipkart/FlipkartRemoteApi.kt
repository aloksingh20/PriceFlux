package com.example.priceflux.data.remote.flipkart

import com.example.priceflux.data.remote.RemoteDto
import com.example.priceflux.util.Resource

interface FlipkartRemoteApi {

    suspend fun getSearchProducts(search: String): Resource<List<RemoteDto>>

    suspend fun getProductsDetails(productUrl: String): Resource<RemoteDto>

}