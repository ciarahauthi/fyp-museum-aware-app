package com.stitchumsdev.fyp.core.model
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
    val dislikes: Int
) {
    fun toObjectModel() = ObjectModel(
        id = this.id,
        title = this.title,
        description = this.description,
        category = this.category,
        childFriendly = this.childFriendly,
        likes = this.likes,
        dislikes = this.dislikes
    )
}