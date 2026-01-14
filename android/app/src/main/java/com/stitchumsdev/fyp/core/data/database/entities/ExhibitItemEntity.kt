package com.stitchumsdev.fyp.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stitchumsdev.fyp.core.model.ObjectModel

@Entity( indices = [Index(value = ["id"], unique = true)] )
data class ExhibitItemEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val childFriendly: Boolean,
    val likes: Int,
    val dislikes: Int,
    val uuid: String,
    val major: Int,
    val minor: Int
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

