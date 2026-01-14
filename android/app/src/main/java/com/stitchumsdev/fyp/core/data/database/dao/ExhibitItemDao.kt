package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExhibitItemDao {
    @Query("SELECT * FROM exhibititementity")
    fun getAll(): Flow<List<ExhibitItemEntity>>

    @Query("SELECT * FROM exhibititementity WHERE uuid = :uuid AND major = :major AND minor = :minor")
    fun getByBeaconInfo(uuid: String, major: Int, minor: Int): Flow<List<ExhibitItemEntity>>

    @Upsert
    suspend fun upsertAll(items: List<ExhibitItemEntity>)

    @Delete
    suspend fun deleteIconItemEntity(iconItemEntity: ExhibitItemEntity)
}