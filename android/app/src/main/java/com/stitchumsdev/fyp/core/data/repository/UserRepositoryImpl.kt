package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.data.remote.ApiService
import com.stitchumsdev.fyp.core.model.ObjectResponse
import com.stitchumsdev.fyp.core.model.UserResponse

class UserRepositoryImpl(
    private val api: ApiService
) : UserRepository {
    override suspend fun getUsers(): List<UserResponse> = api.getUsers()
    override suspend fun getObject(data: List<String>): List<ObjectResponse> = api.getObjects(data)
    override suspend fun getExhibits(): List<ObjectResponse> = api.getExhibits()
}