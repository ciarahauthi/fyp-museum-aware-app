package com.stitchumsdev.fyp.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity( indices = [Index(value = ["id"], unique = true)] )
data class RouteItemEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val nodeIds: List<Int>,
    val stops: List<Int>,
    val imageUrl: String? = null
)