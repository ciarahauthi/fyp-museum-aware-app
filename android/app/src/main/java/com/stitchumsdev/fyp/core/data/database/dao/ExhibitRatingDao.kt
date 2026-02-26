package com.stitchumsdev.fyp.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitRatingEntity

@Dao
interface ExhibitRatingDao {
    @Query("SELECT exhibitId FROM ExhibitRatingEntity")
    suspend fun getAllRatedExhibitIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markRated(exhibit: ExhibitRatingEntity)
}