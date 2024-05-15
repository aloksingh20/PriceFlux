package com.example.priceflux

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf

@SuppressLint("StaticFieldLeak")
object ContextProvider {
    lateinit var context: Context
}

val LocalContext = staticCompositionLocalOf<Context> {
    error("No Context provided")
}
