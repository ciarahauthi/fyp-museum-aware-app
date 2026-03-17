package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity

@Dao
interface ExhibitItemDao {
    @Query("SELECT * FROM exhibititementity")
    suspend fun getAll(): List<ExhibitItemEntity>

    @Query("SELECT * FROM exhibititementity WHERE uuid = :uuid AND major = :major AND minor = :minor")
    suspend fun getByBeaconInfo(uuid: String, major: Int, minor: Int): List<ExhibitItemEntity>

    @Query("DELETE FROM exhibititementity")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(items: List<ExhibitItemEntity>)

    @Delete
    suspend fun deleteIconItemEntity(iconItemEntity: ExhibitItemEntity)
}