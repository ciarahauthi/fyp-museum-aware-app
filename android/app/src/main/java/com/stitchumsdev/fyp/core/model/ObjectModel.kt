package com.stitchumsdev.fyp.core.model

data class ObjectModel(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val childFriendly: Boolean,
    val likes: Int,
    val dislikes: Int,
    val location: Int,
    val uuid: String,
    val major: Int,
    val minor: Int,
    val imageURl: String? = null,
    val canRate: Boolean = false
)