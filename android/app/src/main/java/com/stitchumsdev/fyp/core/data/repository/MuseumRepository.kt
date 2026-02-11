package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel

interface MuseumRepository {
    suspend fun load(): MuseumCache
    suspend fun warmUp()
    fun clearCache()
}

data class MuseumCache(
    val locations: List<LocationModel>,
    val objects: List<ObjectModel>,
    val locationById: Map<Int, LocationModel>,
    val objectsByLocationId: Map<Int, List<ObjectModel>>
)