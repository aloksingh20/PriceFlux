package com.example.priceflux.data.Repository

import com.example.priceflux.data.local.AppDatabase
import com.example.priceflux.data.local.notification.NotificationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
      appDatabase: AppDatabase
){
    private val notificationDao = appDatabase.notificationDao

    suspend fun insert(notification: NotificationEntity) = notificationDao.insert(notification)

    suspend fun delete(notification: NotificationEntity) = notificationDao.delete(notification)

    suspend fun deleteAll() = notificationDao.deleteAll()

    suspend fun getAllNotifications() = notificationDao.getAll()

}