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
    companion object {
        // Dummy Data for dev purposes
        // ToDo remove on deployment
        fun mock(): List<LocationModel> = listOf(
            LocationModel(id = 7, name = "G", x = 0.70f, y = 0.37f),
            LocationModel(id = 5, name = "E", x = 0.62f, y = 0.27f),
            LocationModel(id = 4, name = "D", x = 0.40f, y = 0.38f),
            LocationModel(id = 2, name = "B", x = 0.20f, y = 0.28f),
            LocationModel(id = 3, name = "C", x = 0.20f, y = 0.46f),
            LocationModel(id = 2, name = "B", x = 0.20f, y = 0.28f),
            LocationModel(id = 1, name = "A", x = 0.12f, y = 0.37f),
        )
    }
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