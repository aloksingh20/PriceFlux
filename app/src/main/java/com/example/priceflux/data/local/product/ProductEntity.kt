package com.example.priceflux.data.local.product

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity (
    @PrimaryKey (autoGenerate = true) val id: Int? = null,
    val productName: String,
    var productPrice: String,
    var previousPrice: String,
    val productImage: String,
    val productUrl: String,
    val prodDescription: String,
    val productRating: String
)