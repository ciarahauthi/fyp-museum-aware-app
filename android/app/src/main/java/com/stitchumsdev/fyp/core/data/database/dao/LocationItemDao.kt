package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationItemDao {
    @Query("SELECT * FROM locationitementity")
    suspend fun getAll(): List<LocationItemEntity>

    @Query("SELECT * FROM locationitementity WHERE id = :id")
    suspend fun getLocById(id: Int): LocationItemEntity

    @Upsert
    suspend fun upsertAll(items: List<LocationItemEntity>)
}