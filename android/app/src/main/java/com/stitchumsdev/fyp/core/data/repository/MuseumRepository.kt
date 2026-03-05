package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.RouteItemEntity
import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ExhibitModel
import com.stitchumsdev.fyp.core.model.RouteModel

interface MuseumRepository {
    suspend fun load(): MuseumCache
    suspend fun warmUp()
    fun clearCache()
    suspend fun save(
        exhibits: List<ExhibitItemEntity>,
        locations: List<LocationItemEntity>,
        routes: List<RouteItemEntity>
    )
}

data class MuseumCache(
    val locations: List<LocationModel>,
    val objects: List<ExhibitModel>,
    val routes: List<RouteModel>,
    val locationById: Map<Int, LocationModel>,
    val objectsByLocationId: Map<Int, List<ExhibitModel>>,
    val routeById: Map<Int, RouteModel>,
    val objectsByBeaconId: Map<BeaconId, ExhibitModel>
)