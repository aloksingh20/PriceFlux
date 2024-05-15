package com.example.priceflux.data.remote.amazon

import com.example.priceflux.util.Resource

interface AmazonRemoteApi {

    suspend fun getSearchProducts(search: String): Resource<List<RemoteDto>>

    suspend fun getProductsDetails(productUrl: String): Resource<RemoteDto>

}