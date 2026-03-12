package com.stitchumsdev.fyp.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stitchumsdev.fyp.core.model.ExhibitModel

@Entity( indices = [Index(value = ["id"], unique = true)] )
data class ExhibitItemEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val childFriendly: Boolean,
    val isLoud: Boolean,
    val isCrowded: Boolean,
    val isDark: Boolean,
    val likes: Int,
    val dislikes: Int,
    val uuid: String,
    val major: Int,
    val minor: Int,
    val location: Int,
    val imageUrl: String? = null,
    val createdAt: Long
) {
    // Function to convert db item to model for Ui state.
    fun toObjectModel(canRate: Boolean = false) = ExhibitModel(
        id = this.id,
        title = this.title,
        description = this.description,
        category = this.category,
        childFriendly = this.childFriendly,
        isLoud = this.isLoud,
        isCrowded = this.isCrowded,
        isDark = this.isDark,
        likes = this.likes,
        dislikes = this.dislikes,
        location = this.location,
        uuid = this.uuid,
        major = this.major,
        minor = this.minor,
        imageUrl = this.imageUrl,
        canRate = canRate,
        createdAt = this.createdAt
    )
}

