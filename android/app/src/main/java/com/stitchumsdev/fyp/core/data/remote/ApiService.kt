package com.stitchumsdev.fyp.core.data.remote

import com.stitchumsdev.fyp.core.model.UserResponse
import retrofit2.http.GET

interface ApiService {
    @GET("api/users/")
    suspend fun getUsers(): List<UserResponse>
}