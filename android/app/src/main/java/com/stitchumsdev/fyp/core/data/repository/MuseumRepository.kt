package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.model.RouteModel

interface MuseumRepository {
    suspend fun load(): MuseumCache
    suspend fun warmUp()
    fun clearCache()
}

data class MuseumCache(
    val locations: List<LocationModel>,
    val objects: List<ObjectModel>,
    val routes: List<RouteModel>,
    val locationById: Map<Int, LocationModel>,
    val objectsByLocationId: Map<Int, List<ObjectModel>>,
    val routeById: Map<Int, RouteModel>,
    val objectsByBeaconId: Map<BeaconId, ObjectModel>
)