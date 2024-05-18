package com.example.priceflux.util

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        @TypeConverter
        @JvmStatic
        fun toLocalDateTime(value: String?): LocalDateTime? {
            return value?.let { LocalDateTime.parse(it, formatter) }
        }

        @TypeConverter
        @JvmStatic
        fun fromLocalDateTime(date: LocalDateTime?): String? {
            return date?.format(formatter)
        }
    }
}
