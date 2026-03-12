package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.AllRoutesResponse
import com.stitchumsdev.fyp.core.model.BeaconEventsRequest
import com.stitchumsdev.fyp.core.model.HomeResponse
import com.stitchumsdev.fyp.core.model.LocationResponse
import com.stitchumsdev.fyp.core.model.ExhibitResponse

interface UserRepository {
    suspend fun getExhibits(): List<ExhibitResponse>
    suspend fun getLocations(): List<LocationResponse>
    suspend fun getRoutes(): List<AllRoutesResponse>
    suspend fun getRoute(current: Int, targets: List<Int>): List<Int>
    suspend fun sendExhibitRating(id: Int, rating: Boolean)
    suspend fun getHomeContent(): HomeResponse
    suspend fun sendBeaconEvents(body: BeaconEventsRequest)
}