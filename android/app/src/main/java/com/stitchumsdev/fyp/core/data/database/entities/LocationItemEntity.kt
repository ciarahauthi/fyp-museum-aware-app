package com.stitchumsdev.fyp.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stitchumsdev.fyp.core.model.LocationModel

@Entity( indices = [Index(value = ["id"], unique = true)] )
data class LocationItemEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val x: Float,
    val y: Float
) {
    fun toLocationModel() = LocationModel(
        id = this.id,
        name = this.name,
        x = this.x,
        y = this.y,
    )
}