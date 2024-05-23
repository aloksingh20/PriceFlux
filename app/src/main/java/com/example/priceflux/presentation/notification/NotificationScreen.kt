package com.example.priceflux.presentation.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.priceflux.data.local.notification.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewmodel: NotificationViewmodel,
    navController: NavController
) {
    val state = viewmodel.state
    val snackbarHostState = remember { SnackbarHostState() }

    var isDialogOpen by remember { mutableStateOf(false) }
    var notificationList by remember { mutableStateOf(state.notificationInfo) }
    var currentNotificationEntity by remember { mutableStateOf<NotificationEntity?>(null) }

    // Custom function to show the Snackbar with a NotificationEntity
    suspend fun showSnackbarWithEntity(message: String, notificationEntity: NotificationEntity) {
        currentNotificationEntity = notificationEntity
        snackbarHostState.showSnackbar(message)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Notifications") },
                actions = {
                    IconButton(onClick = {
                        isDialogOpen = true
                    }) {
                        Icon(imageVector = Icons.Filled.ClearAll, contentDescription = "Clear Notifications")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            notificationList = state.notificationInfo
            Box {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (notificationList.isEmpty()) {
                    Text(text = "No Notifications", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn {
                        items(notificationList) { notification ->
                            NotificationListItem(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(5.dp),
                                notification = notification,
                                onItemClick = {
                                    navController.navigate("watchlist")
                                },
                                onItemSwiped = { notificationEntity ->
                                    viewmodel.deleteNotification(notificationEntity)
                                    notificationList = notificationList.toMutableList().apply { remove(notificationEntity) }
                                    CoroutineScope(Dispatchers.Main).launch {
                                        showSnackbarWithEntity("Notification Deleted", notificationEntity)
                                    }
                                }
                            )
                        }
                    }
                }

                if (isDialogOpen) {
                    ConfirmNotificationDeleteDialog(
                        onDismiss = { isDialogOpen = false },
                        onConfirm = {
                            viewmodel.clearAllNotification()
                            notificationList = mutableListOf()
                            isDialogOpen = false
                        }
                    )
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) { snackbarData ->
                    Snackbar {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = snackbarData.visuals.message,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            TextButton(
                                onClick = {
                                    snackbarData.dismiss()
                                    currentNotificationEntity?.let { notificationEntity ->
                                        notificationList = notificationList.toMutableList().apply { add(0, notificationEntity) }
                                        viewmodel.addNotification(notificationEntity)
                                    }
                                },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Undo", fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmNotificationDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "No")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = "Yes")
            }
        },
        title = { Text("Clear Notifications?") },
        text = { Text("Are you sure you want to clear notifications?") }
    )
}
