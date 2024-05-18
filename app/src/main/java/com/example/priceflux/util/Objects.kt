package com.example.priceflux.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Objects {
    const val AMAZON_URL = "https://www.amazon.in"
    const val FLIPKART_URL = "https://www.flipkart.com"

}
val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE products ADD COLUMN id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0")
    }
}