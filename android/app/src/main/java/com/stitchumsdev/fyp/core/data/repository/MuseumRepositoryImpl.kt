package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.RouteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class MuseumRepositoryImpl(
    private val appDatabase: AppDatabase
) : MuseumRepository {

    private val mutex = Mutex()
    @Volatile private var cache: MuseumCache? = null

    override suspend fun load(): MuseumCache = mutex.withLock {
        cache?.let { return it }

        val built = withContext(Dispatchers.IO) {
            val ratedIds = appDatabase.exhibitRatingDao()
                .getAllRatedExhibitIds()
                .toSet()

            val objects = appDatabase.exhibitItemDao()
                .getAll()
                .map { entity ->
                    entity.toObjectModel(canRate = entity.id !in ratedIds)
                }
            val locations = appDatabase.locationItemDao().getAll().map { it.toLocationModel() }
            val routeEntities = appDatabase.routeItemDao().getAll()

            val locationById = locations.associateBy { it.id }
            val objectById = objects.associateBy { it.id }

            val objectsByLocationId = objects.groupBy { it.location }

            val routes = routeEntities.map { route ->
                RouteModel(
                    id = route.id,
                    name = route.name,
                    description = route.description,
                    nodes = route.nodeIds.mapNotNull { locationById[it] }, // location ids -> LocationModel
                    stops = route.stops.mapNotNull { objectById[it] } // object ids -> ObjectModel
                )
            }
            val routeById = routes.associateBy { it.id }

            val objectsByBeaconId = objects.associateBy { obj ->
                BeaconId(obj.uuid, obj.major, obj.minor)
            }

            MuseumCache(
                locations = locations,
                objects = objects,
                routes = routes,
                locationById = locationById,
                objectsByLocationId = objectsByLocationId,
                routeById = routeById,
                objectsByBeaconId = objectsByBeaconId
            )
        }

        built.also { cache = it }
    }

    override suspend fun warmUp() { load() }

    override fun clearCache() { cache = null }
}