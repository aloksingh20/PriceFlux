package com.example.priceflux.presentation.watchlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priceflux.data.Repository.WatchlistRepository
import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.data.mapper.toProductEntity
import com.example.priceflux.data.remote.RemoteDto
import com.example.priceflux.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
):ViewModel() {

    var state  by mutableStateOf(WatchlistInfoState())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
        if(_searchQuery.value.matches(regex = Regex("([0-9]+)"))){
            searchProduct()
        }
        if(searchQuery.value.isEmpty()){
            state = state.copy(
                prodInfo = mutableListOf()
            )
        }

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
            searchProduct()
        }
    }
    fun addToWatchlist(product: RemoteDto) {
        viewModelScope.launch {
            val productEntity = product.toProductEntity()
            watchlistRepository.insertProduct(productEntity)
            state = state.copy(
                prodInfo = state.prodInfo.toMutableList().apply {
                    add(productEntity)
                }
            )
        }
    }
    fun getWatchlist() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = when(val result = watchlistRepository.getAllProducts()){
                is Resource.Success -> {
                    state.copy(
                        prodInfo = result.data?.toMutableList() ?: mutableListOf(),
                        isLoading = false
                    )
                }

                else -> {
                    state.copy(
                        isLoading = false,
                        error = "Could not load data"
                    )
                }
            }
        }
    }
    fun searchProduct(query:String = searchQuery.value) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = when(val result = watchlistRepository.searchProducts(query)){
                is Resource.Success -> {
                    state.copy(
                        prodInfo = result.data?.toMutableList()?: mutableListOf(),
                        isLoading = false
                    )
                }

                else -> {
                    state.copy(
                        isLoading = false,
                        error = "Could not load data"
                    )
                }
            }
        }
    }
    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {

            watchlistRepository.deleteProduct(product)
            state = state.copy(
                prodInfo = state.prodInfo.filter { it.id != product.id }.toMutableList()
            )

        }
    }

    init {
        getWatchlist()
    }

}