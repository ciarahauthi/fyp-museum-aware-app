package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.UserResponse

interface UserRepository {
    suspend fun getUsers(): List<UserResponse>
}