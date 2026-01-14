package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.ObjectResponse
import com.stitchumsdev.fyp.core.model.UserResponse

interface UserRepository {
    suspend fun getUsers(): List<UserResponse>
    suspend fun getObject(data: List<String>): List<ObjectResponse>
    suspend fun getExhibits(): List<ObjectResponse>
}