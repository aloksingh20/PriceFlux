package com.example.priceflux.data.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.priceflux.MainActivity
import com.example.priceflux.R
import com.example.priceflux.data.Repository.NotificationRepository
import com.example.priceflux.data.Repository.WatchlistRepository
import com.example.priceflux.data.local.AppDatabase
import com.example.priceflux.data.local.notification.NotificationEntity
import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.data.remote.RemoteDto
import com.example.priceflux.data.remote.scrapper.AmazonScrapper
import com.example.priceflux.data.remote.scrapper.FlipkartScraper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltWorker
class ScraperWorker @AssistedInject constructor(
    @Assisted  appContext: Context,
    @Assisted  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    @Inject
    lateinit var amazonScraper: AmazonScrapper
    @Inject
    lateinit var flipkartScraper: FlipkartScraper
    @Inject
    lateinit var watchlistRepository: WatchlistRepository
    @Inject
    lateinit var notificationRepository: NotificationRepository

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("ScraperWorker", "Scraping started")
        notifyUser(ProductEntity(0,"","","","","","",""))
        try {
            val watchlist = watchlistRepository.getAllProducts()

            if (!watchlist.data.isNullOrEmpty()) {
                val result = watchlist.data

                result.forEach { product ->
                    val priceFlipkart = flipkartScraper.getProductsDetails(product.productUrl)

                    if (priceFlipkart.data?.productPrice!! < product.productPrice) {
                        product.previousPrice = product.productPrice
                        product.productPrice = priceFlipkart.data.productPrice
                        watchlistRepository.insertProduct(product)
                        notifyUser(product)
                        val notification = NotificationEntity(
                            timestamp = LocalDateTime.now(),
                            body = "",
                            title = "Price Drop Alert!",
                            description = "",
                            imageUrl = "",
                            id = 0
                        )
                        notificationRepository.insert(notification)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun notifyUser(product: ProductEntity) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.example.com/watchlist".toUri(),
            applicationContext,
            MainActivity::class.java
        )
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, deepLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val channel = NotificationChannel(
            "PRICE_DROP_CHANNEL",
            "channelName",
            NotificationManager.IMPORTANCE_HIGH

        )
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "PRICE_DROP_CHANNEL")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentTitle("Price Drop Alert!")
            .setContentText("Price for ${product.productName} has dropped.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.createNotificationChannel(channel)
            notificationManager.notify(product.id.hashCode(), notificationBuilder.build())
    }
    init {
        Log.d("ScraperWorker", "Worker initialized")
    }
}
