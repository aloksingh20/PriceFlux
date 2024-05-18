package com.example.priceflux.presentation.notification

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewmodel: NotificationViewmodel
){
    val state = viewmodel.state

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Notifications") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.ClearAll, contentDescription = "Clear Notifications")
                    }
                }
            )
        }
    ){ paddingValues ->

        Surface(modifier = Modifier.padding(paddingValues)) {

        }

    }

}