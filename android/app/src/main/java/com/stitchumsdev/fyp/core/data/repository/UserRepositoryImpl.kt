package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.data.remote.ApiService
import com.stitchumsdev.fyp.core.model.AllRoutesResponse
import com.stitchumsdev.fyp.core.model.LocationResponse
import com.stitchumsdev.fyp.core.model.ObjectResponse
import com.stitchumsdev.fyp.core.model.RateRequest
import com.stitchumsdev.fyp.core.model.UserResponse

class UserRepositoryImpl(
    private val api: ApiService
) : UserRepository {
    override suspend fun getUsers(): List<UserResponse> = api.getUsers()
    override suspend fun getObject(data: List<String>): List<ObjectResponse> = api.getObjects(data)
    override suspend fun getExhibits(): List<ObjectResponse> = api.getExhibits()
    override suspend fun getLocations(): List<LocationResponse> = api.getLocations()
    override suspend fun getRoutes(): List<AllRoutesResponse> = api.getRoutes()
    override suspend fun getRoute(current: Int, targets: List<Int>): List<Int> = api.getRoute(
        current = current,
        targets = targets
    )
    override suspend fun sendExhibitRating(id: Int, rating: Boolean) { api.sendRating(
        RateRequest(
            exhibitId = id,
            rating = rating
        )
    ) }
}