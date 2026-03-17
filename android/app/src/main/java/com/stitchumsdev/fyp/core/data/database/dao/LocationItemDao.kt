package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity

@Dao
interface LocationItemDao {
    @Query("SELECT * FROM locationitementity")
    suspend fun getAll(): List<LocationItemEntity>

    @Query("SELECT * FROM locationitementity WHERE id = :id")
    suspend fun getLocById(id: Int): LocationItemEntity

    @Query("DELETE FROM locationitementity")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(items: List<LocationItemEntity>)
}