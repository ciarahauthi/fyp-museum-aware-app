package com.stitchumsdev.fyp.core.model
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.RouteItemEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    @SerialName("first_name") val firstName: String,
    val surname: String,
    val email: String
)

@Serializable
data class ObjectResponse(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    @SerialName("child_friendly") val childFriendly: Boolean,
    val likes: Int,
    val dislikes: Int,
    val uuid: String,
    val major: Int,
    val minor: Int,
    val location: Int
) {
    fun toExhibitEntity() = ExhibitItemEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        category = this.category,
        childFriendly = this.childFriendly,
        likes = this.likes,
        dislikes = this.dislikes,
        uuid = this.uuid,
        major = this.major,
        minor = this.minor,
        location = this.location
    )
}

@Serializable
data class LocationResponse(
    val id: Int,
    val name: String,
    val x: Float,
    val y: Float
) {
    fun toLocationItemEntity() = LocationItemEntity(
        id = this.id,
        name = this.name,
        x = this.x,
        y = this.y
    )
}

@Serializable
data class AllRoutesResponse(
    val id: Int,
    val name: String,
    val description: String,
    @SerialName("node_ids") val nodeIds: List<Int>,
    val stops: List<Int>
) {
    fun toRouteItemEntity() = RouteItemEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        nodeIds = this.nodeIds,
        stops = this.stops
    )
}