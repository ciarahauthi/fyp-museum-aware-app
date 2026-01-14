package com.stitchumsdev.fyp.core.data.remote

import com.stitchumsdev.fyp.core.model.ObjectResponse
import com.stitchumsdev.fyp.core.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/users/")
    suspend fun getUsers(): List<UserResponse>

    @GET("api/exhibits/lookup_many")
    suspend fun getObjects(
        @Query("data") data: List<String>
    ): List<ObjectResponse>

    @GET("api/exhibits/")
    suspend fun getExhibits(): List<ObjectResponse>
}