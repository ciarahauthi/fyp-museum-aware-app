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