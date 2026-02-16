package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.stitchumsdev.fyp.core.data.database.entities.RouteItemEntity

@Dao
interface RouteItemDao {
    @Query("SELECT * FROM routeitementity")
    suspend fun getAll(): List<RouteItemEntity>

    @Upsert
    suspend fun upsertAll(items: List<RouteItemEntity>)
}