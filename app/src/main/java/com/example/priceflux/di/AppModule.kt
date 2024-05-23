package com.example.priceflux.di

import android.app.Application
import androidx.room.Room
import com.example.priceflux.data.Repository.NotificationRepository
import com.example.priceflux.data.Repository.WatchlistRepository
import com.example.priceflux.data.local.AppDatabase
import com.example.priceflux.data.remote.scrapper.AmazonScrapper
import com.example.priceflux.data.remote.scrapper.FlipkartScraper
import com.example.priceflux.data.remote.scrapper.ScrapperExecuter
import com.example.priceflux.util.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "appDb.db")
            .addMigrations(MIGRATION_2_3)
            .build()
    }
    @Provides
    @Singleton
    fun provideScrapperExecuter(): ScrapperExecuter = ScrapperExecuter()

    @Provides
    @Singleton
    fun provideAmazonScrapper(scrapperExecuter: ScrapperExecuter): AmazonScrapper = AmazonScrapper(scrapperExecuter);

    @Provides
    @Singleton
    fun provideFlipkartScrapper(scrapperExecuter: ScrapperExecuter): FlipkartScraper = FlipkartScraper(scrapperExecuter);

    @Provides
    @Singleton
    fun provideRepository(appDatabase: AppDatabase): WatchlistRepository {
        return WatchlistRepository(appDatabase)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(appDatabase: AppDatabase): NotificationRepository {
        return NotificationRepository(appDatabase)
    }




}