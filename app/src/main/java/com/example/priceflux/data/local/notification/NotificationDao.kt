package com.example.priceflux.data.local.notification

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications")
    suspend fun getAll(): List<NotificationEntity>

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(notification: NotificationEntity)


}