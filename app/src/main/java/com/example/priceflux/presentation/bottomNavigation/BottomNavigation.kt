package com.example.priceflux.presentation.bottomNavigation

import CameraPreview
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.priceflux.presentation.HomeScreen
import com.example.priceflux.presentation.PriceViewModel
import com.example.priceflux.presentation.notification.NotificationScreen
import com.example.priceflux.presentation.notification.NotificationViewmodel
import com.example.priceflux.presentation.watchlist.WatchlistScreen
import com.example.priceflux.presentation.watchlist.WatchlistViewModel

@Composable
fun BottomNavigation(
    context:Context
) {
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Watchlist",
            selectedIcon = Icons.Filled.RemoveRedEye,
            unselectedIcon = Icons.Outlined.RemoveRedEye
        ),
        BottomNavigationItem(
            title = "Notification",
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.Notifications
        )
    )
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                NavigationBar (containerColor = Color.Black){
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.title) // Replace "home" with your actual destination screen name for Home
                            },
                            label = { Text(text = item.title
                                , color = if(index==selectedItemIndex){
                                    Color.Green
                                }else Color.White
                            ) },
                            icon = {

                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                    , tint = if(index==selectedItemIndex){
                                        Color.Green
                                    }else Color.White
                                )
                            }
                        )
                    }
                }
            }
        ) {innerPadding->
            // This slot can be used to display content based on the current destination
            val priceViewModel = hiltViewModel <PriceViewModel>()
            val watchlistViewModel = hiltViewModel <WatchlistViewModel>()
//            val notificationViewModel = hiltViewModel <Noti>()Noti

            Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()) {
                Column {
                    NavHost(
                        navController = navController,
                        startDestination = "Home"
                    ) {
                        composable("Home") {
                            HomeScreen(
                                context = context,
                                navController = navController,
                                viewModel = priceViewModel,
                                watchlistViewModel = watchlistViewModel
                            )
                        }
                        composable("qrscanner") {
                            CameraPreview(
                                Modifier.fillMaxSize(),
                                viewModel = priceViewModel,
                                navController
                            )
                        }
                        composable("notification") {
                            val notificationViewModel = hiltViewModel <NotificationViewmodel>()
                            NotificationScreen(notificationViewModel
                            )
                        }
                        composable("watchlist"){
                            // WatchlistScreen()
                            WatchlistScreen(watchlistViewModel)
                        }
                    }
                }
            }
        }
    }
}