package com.example.priceflux.presentation.watchlist

import androidx.compose.runtime.mutableStateOf
import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.data.remote.RemoteDto

data class WatchlistInfoState(
    val prodInfo:MutableList<ProductEntity> = mutableListOf(),
    val isLoading:Boolean = false,
    val error:String = ""

)