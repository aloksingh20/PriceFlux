package com.example.priceflux.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data, "Success")
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}