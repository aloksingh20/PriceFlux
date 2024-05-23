package com.example.priceflux.presentation.home

import com.example.priceflux.data.remote.RemoteDto

data class SearchInfoState(
    val amazonInfo:List<RemoteDto> = emptyList(),
    val flipkartInfo:List<RemoteDto> = emptyList(),
    val isLoading:Boolean = false,
    val amazonError:String = "",
    val flipkartError:String = ""

)