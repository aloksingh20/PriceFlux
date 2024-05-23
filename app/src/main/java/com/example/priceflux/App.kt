package com.example.priceflux

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.priceflux.data.workManager.ScraperWorker
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App:Application() , Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        ContextProvider.context = this.applicationContext
        scheduleScraperWorker()
    }
    private fun scheduleScraperWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ScraperWorker>(3, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()


        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "priceScraper",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}