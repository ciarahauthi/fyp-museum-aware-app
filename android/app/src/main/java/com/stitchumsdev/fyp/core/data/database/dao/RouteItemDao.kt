package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.stitchumsdev.fyp.core.data.database.entities.RouteItemEntity

@Dao
interface RouteItemDao {
    @Query("SELECT * FROM routeitementity")
    suspend fun getAll(): List<RouteItemEntity>

    @Query("DELETE FROM routeitementity")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(items: List<RouteItemEntity>)
}