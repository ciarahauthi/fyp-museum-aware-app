package com.stitchumsdev.fyp.core.model

data class ExhibitModel(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val childFriendly: Boolean,
    val isLoud: Boolean,
    val isCrowded: Boolean,
    val isDark: Boolean,
    val likes: Int,
    val dislikes: Int,
    val location: Int,
    val uuid: String,
    val major: Int,
    val minor: Int,
    val imageUrl: String? = null,
    val canRate: Boolean = false,
    val createdAt: Long
)