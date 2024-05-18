package com.example.priceflux.data.workManager

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.priceflux.MainActivity
import com.example.priceflux.R
import com.example.priceflux.data.Repository.NotificationRepository
import com.example.priceflux.data.Repository.WatchlistRepository
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
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val amazonScraper: AmazonScrapper,
    private val flipkartScraper: FlipkartScraper,
    private val watchlistRepository: WatchlistRepository,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("ScraperWorker", "Scraping started")

        try {
            val watchlist = watchlistRepository.getAllProducts()

            if (!watchlist.data.isNullOrEmpty()) {
                val result = watchlist.data

                result?.forEach { product ->
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
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.example.com/watchlist".toUri(),
            appContext,
            MainActivity::class.java
        )
        val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext, 0, deepLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(appContext, "PRICE_DROP_CHANNEL")
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentTitle("Price Drop Alert!")
            .setContentText("Price for ${product.productName} has dropped.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(product.id.hashCode(), notificationBuilder.build())
    }
}
