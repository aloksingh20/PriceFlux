package com.example.priceflux.presentation.notification

import com.example.priceflux.data.local.notification.NotificationEntity
import com.example.priceflux.data.remote.RemoteDto

data class NotificationInfoState(
    val notificationInfo:MutableList<NotificationEntity> = mutableListOf(),
    val isLoading:Boolean = false,
    val error:String = ""

)
