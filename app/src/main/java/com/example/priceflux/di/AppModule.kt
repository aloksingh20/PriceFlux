package com.example.priceflux.di

import com.example.priceflux.data.remote.scrapper.AmazonScrapper
import com.example.priceflux.data.remote.scrapper.FlipkartScraper
import com.example.priceflux.data.remote.scrapper.ScrapperExecuter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideScrapperExecuter(): ScrapperExecuter = ScrapperExecuter()

    @Provides
    @Singleton
    fun provideAmazonScrapper(scrapperExecuter: ScrapperExecuter): AmazonScrapper = AmazonScrapper(scrapperExecuter);

    @Provides
    @Singleton
    fun provideFlipkartScrapper(scrapperExecuter: ScrapperExecuter): FlipkartScraper = FlipkartScraper(scrapperExecuter);


}