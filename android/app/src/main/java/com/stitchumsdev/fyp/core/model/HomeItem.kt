package com.stitchumsdev.fyp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    val id: Int,
    val title: String,
    @SerialName("image_url") val imageUrl: String? = null,
    val description: String
)