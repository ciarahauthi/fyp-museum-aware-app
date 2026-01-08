package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.data.remote.ApiService
import com.stitchumsdev.fyp.core.model.UserResponse

class UserRepositoryImpl(
    private val api: ApiService
) : UserRepository {
    override suspend fun getUsers(): List<UserResponse> = api.getUsers()
}