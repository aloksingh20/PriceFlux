package com.example.priceflux.data.Repository

import com.example.priceflux.data.local.AppDatabase
import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.data.mapper.toRemoteDto
import com.example.priceflux.data.remote.RemoteDto
import com.example.priceflux.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val appDatabase: AppDatabase
) {
    private val prodDao = appDatabase.productDao

    suspend fun insertProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            prodDao.insertProduct(product)
        }
    }

    suspend fun searchProducts(query: String) :Resource<List<ProductEntity>>{
            return try {
                val products = prodDao.searchProduct(query)
                Resource.Success(products)
            } catch (e:Exception){
                Resource.Error("Could not load data" )
            }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        withContext(Dispatchers.IO) {
            prodDao.deleteProduct(product)
        }
    }

    suspend fun getAllProducts():Resource<List<ProductEntity>>{
        return try {
            val products = prodDao.getAllProducts()
            Resource.Success(products)
        }
        catch (e:Exception){
            Resource.Error("Could not load data")
        }
    }


}