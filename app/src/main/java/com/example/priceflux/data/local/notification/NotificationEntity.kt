package com.example.priceflux.data.local.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val description: String?,
    val timestamp: LocalDateTime
)
