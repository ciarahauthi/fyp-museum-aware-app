package com.stitchumsdev.fyp.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stitchumsdev.fyp.core.model.RouteModel

@Entity( indices = [Index(value = ["id"], unique = true)] )
data class RouteItemEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val nodeIds: List<Int>
) {
    fun toRouteModel() = RouteModel(
        id = this.id,
        name = this.name,
        description = this.description,
        nodeIds = this.nodeIds
    )
}