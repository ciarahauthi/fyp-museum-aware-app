package com.stitchumsdev.fyp.core.data.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class RouteConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromIntList(value: List<Int>?): String =
        json.encodeToString(value ?: emptyList())

    @TypeConverter
    fun toIntList(value: String?): List<Int> =
        if (value.isNullOrBlank()) emptyList()
        else json.decodeFromString(value)
}