package com.example.priceflux.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity (
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val price: Double
)