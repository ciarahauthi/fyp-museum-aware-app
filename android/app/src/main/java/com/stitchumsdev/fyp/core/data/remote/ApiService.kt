package com.stitchumsdev.fyp.core.data.remote

import com.stitchumsdev.fyp.core.model.AllRoutesResponse
import com.stitchumsdev.fyp.core.model.BeaconEventsRequest
import com.stitchumsdev.fyp.core.model.HomeResponse
import com.stitchumsdev.fyp.core.model.LocationResponse
import com.stitchumsdev.fyp.core.model.ExhibitResponse
import com.stitchumsdev.fyp.core.model.RateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("api/exhibits")
    suspend fun getExhibits(): List<ExhibitResponse>

    @GET("api/routes/locations")
    suspend fun getLocations(): List<LocationResponse>

    @GET("api/routes")
    suspend fun getRoutes(): List<AllRoutesResponse>

    @GET("/api/route")
    suspend fun getRoute(
        @Query("current") current: Int,
        @Query("targets") targets: List<Int>
    ): List<Int>

    @POST("/api/exhibits/rate")
    suspend fun sendRating(
        @Body body: RateRequest
    ): Response<Unit>

    @GET("/api/home")
    suspend fun getHomeContent(): HomeResponse
    @POST("/api/beacon_events")
    suspend fun sendBeaconEvents(@Body body: BeaconEventsRequest)
}