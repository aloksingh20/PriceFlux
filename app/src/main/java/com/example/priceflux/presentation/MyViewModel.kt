package com.example.priceflux.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priceflux.data.remote.amazon.RemoteDto
import com.example.priceflux.data.remote.scrapper.AmazonScrapper
import com.example.priceflux.data.remote.scrapper.FlipkartScraper
import com.example.priceflux.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceViewModel @Inject constructor(
    private val amazonScrapper: AmazonScrapper,
    private val flipkartScrapper: FlipkartScraper
) : ViewModel() {

    var state by mutableStateOf(SearchInfoState())
        private set

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()



    fun scrapeAmazonPrice() {
        viewModelScope.launch(Dispatchers.IO) {
            val searchQuery = searchQuery.value
            println("Searching for: $searchQuery")
            delay(1000)
            state = state.copy(isLoading = true)
            val amazonResult = async { amazonScrapper.getSearchProducts(searchQuery) }
            val flipkartResult = async { flipkartScrapper.getSearchProducts(searchQuery) }

            val amazonProducts = amazonResult.await()
            val flipkartProducts = flipkartResult.await()

            state = if (amazonProducts is Resource.Success && flipkartProducts is Resource.Success) {
                val amazonSize = amazonProducts.data?.size ?: 0
                val flipkartSize = flipkartProducts.data?.size ?: 0
                val size = minOf(amazonSize, flipkartSize)
                val amazonInfo = amazonProducts.data?.subList(0, size) ?: emptyList()
                val flipkartInfo = flipkartProducts.data?.subList(0, size) ?: emptyList()
//
//                val amazonList = mutableListOf<RemoteDto>()
//
//                val flipkartList = mutableListOf<RemoteDto>()
//
//                for (i in 0 until size) {
//                    val amazonProduct :Set<String> = amazonInfo[i].productName.split("").toSet()
//                    val flipkartProduct :Set<String> = flipkartInfo[i].productName.split("").toSet()
//
//                    val score = jaccardSimilarity( amazonProduct, flipkartProduct)
//                    if(score>0.5){
//                        amazonList.add(amazonInfo[i])
//                        flipkartList.add(flipkartInfo[i])
//                    }
//                }
//                Log.d("amazon", amazonList.toString())
//                Log.d("flipkart", flipkartList.toString())

                state.copy(

                    amazonInfo = amazonInfo,
                    flipkartInfo = flipkartInfo,
                    isLoading = false
                )

            } else {
                // Handle errors
                state.copy(
                    amazonError = (amazonProducts as? Resource.Error)?.message ?: "An unexpected error occurred",
                    flipkartError = (flipkartProducts as? Resource.Error)?.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text

    }

    fun onToggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onSearchTextChange("")
        }
    }
    fun onSearch(text:String){
        _searchQuery.value=text
        if(text.isNotEmpty()) {
            scrapeAmazonPrice()
        }
    }

    init {
        viewModelScope.launch {
            // Debounce search trigger with a delay of 500 milliseconds
            searchQuery.debounce(500)
                .filter { it.isNotEmpty() } // Only trigger search for non-empty text
                .collect { query ->
//                    searchSongs(query)
                }
        }
    }
    // Function to calculate Jaccard similarity between two sets of strings
    private fun jaccardSimilarity(set1: Set<String>, set2: Set<String>): Double {
        val intersectionSize = set1.intersect(set2).size
        val unionSize = set1.union(set2).size
        return intersectionSize.toDouble() / unionSize.toDouble()
    }

}
