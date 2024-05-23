package com.example.priceflux.presentation.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.priceflux.data.Repository.NotificationRepository
import com.example.priceflux.data.local.notification.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewmodel @Inject constructor(
    private val notificationRepository: NotificationRepository
):ViewModel(){

    var state by mutableStateOf(NotificationInfoState())

    init {
        getNotification()
    }

    private fun getNotification(){
        viewModelScope.launch {
            val notification = notificationRepository.getAllNotifications()
            state = state.copy(
                 notificationInfo = notification.toMutableList()
            )
        }
    }

    fun deleteNotification(notification: NotificationEntity){
        viewModelScope.launch {
            notificationRepository.delete(notification)
            state = state.copy(notificationInfo = state.notificationInfo.filter { it.id != notification.id }.toMutableList())
        }
    }
    fun clearAllNotification(){
        viewModelScope.launch {
            notificationRepository.deleteAll()
        }
    }
    fun addNotification(notification: NotificationEntity){
        viewModelScope.launch {

            notificationRepository.insert(notification)
            state =state.copy(notificationInfo = state.notificationInfo.apply {  add(notification)  })
        }
    }

}