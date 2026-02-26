package com.stitchumsdev.fyp.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["exhibitId"], unique = true)])
data class ExhibitRatingEntity(
    @PrimaryKey val exhibitId: Int,
    val ratedDate: String
)