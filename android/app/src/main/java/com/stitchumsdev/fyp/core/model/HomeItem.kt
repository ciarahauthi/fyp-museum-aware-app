package com.stitchumsdev.fyp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    val id: Int,
    val title: String,
    val imageUrl: String? = null,
    val description: String
)