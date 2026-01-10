package com.stitchumsdev.fyp.core.model

data class ObjectModel(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val childFriendly: Boolean,
    val likes: Int,
    val dislikes: Int
)