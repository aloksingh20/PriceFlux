package com.example.priceflux.data.local.product

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(productEntity: ProductEntity)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Delete
    suspend fun deleteProduct(productEntity: ProductEntity)

    @Query("""
        SELECT * FROM products
        WHERE productName = :query OR productName LIKE '%' || :query || '%'
    """)
    suspend fun searchProduct(query: String): List<ProductEntity>

}