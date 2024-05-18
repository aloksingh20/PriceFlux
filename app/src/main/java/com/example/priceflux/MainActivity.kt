package com.example.priceflux

import CameraPreview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.priceflux.presentation.HomeScreen
import com.example.priceflux.presentation.NextScreen
import com.example.priceflux.presentation.PriceViewModel
import com.example.priceflux.presentation.bottomNavigation.BottomNavigation
import com.example.priceflux.ui.theme.PriceFluxTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PriceFluxTheme {
                BottomNavigation(context = this@MainActivity)
            }
        }
    }
}
