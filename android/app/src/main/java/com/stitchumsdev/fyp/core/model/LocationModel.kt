package com.stitchumsdev.fyp.core.model

data class LocationModel(
    val id: Int,
    val name: String,
    val x: Float,
    val y: Float
) {
    fun toHmPoint() = RoomHeatPoint(
        id = this.id,
        name = this.name,
        x = this.x,
        y = this.y
    )
}

data class RoomHeatPoint(
    val id: Int,
    val name: String,
    val x: Float,
    val y: Float,
    val weight: Float = 1f,
    val radiusPx: Float = 35f
) {
    fun toLocationModel() = LocationModel(
        id = this.id,
        name = this.name,
        x = this.x,
        y = this.y
    )
}