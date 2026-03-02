package com.stitchumsdev.fyp.core.model
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.RouteItemEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
    val location: Int,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String
) {
    @OptIn(ExperimentalTime::class)
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
        location = this.location,
        imageUrl = this.imageUrl,
        createdAt = Instant.parse(createdAt).toEpochMilliseconds()
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
    val stops: List<Int>,
    @SerialName("image_url") val imageUrl: String? = null
) {
    fun toRouteItemEntity() = RouteItemEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        nodeIds = this.nodeIds,
        stops = this.stops,
        imageUrl = this.imageUrl
    )
}

@Serializable
data class RateRequest(
    @SerialName("exhibit_id") val exhibitId: Int,
    val rating: Boolean
)

@Serializable
data class HomeResponse(
    @SerialName("top_section") val topSection: List<HomeItem> = emptyList(),
    @SerialName("mid_section") val midSection: HomeItem? = null,
    @SerialName("bottom_section") val bottomSection: List<HomeItem> = emptyList()
)

@Serializable
data class BeaconEvent(
    @SerialName("beacon_uuid") val beaconUuid: String,
    @SerialName("beacon_major") val beaconMajor: Int,
    @SerialName("beacon_minor") val beaconMinor: Int,
    val rssi: Int,
    @SerialName("tx_power") val txPower: Int,
    @SerialName("recorded_at")val recordedAt: Long // Epoch millis
)

@Serializable
data class BeaconEventsRequest(
    @SerialName("session_id") val sessionId: String,
    val events: List<BeaconEvent>
)