package com.example.priceflux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.priceflux.presentation.bottomNavigation.BottomNavigation
import com.example.priceflux.ui.theme.PriceFluxTheme
import dagger.hilt.android.AndroidEntryPoint

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
