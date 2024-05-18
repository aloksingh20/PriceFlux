package com.example.priceflux.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.priceflux.data.local.notification.NotificationDao
import com.example.priceflux.data.local.notification.NotificationEntity
import com.example.priceflux.data.local.product.ProductDao
import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.util.Converters

@Database(entities = [ProductEntity::class, NotificationEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase:RoomDatabase() {
    abstract val productDao: ProductDao
    abstract val notificationDao: NotificationDao


}