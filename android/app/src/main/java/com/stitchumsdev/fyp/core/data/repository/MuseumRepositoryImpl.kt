package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
            val objects = appDatabase.exhibitItemDao().getAll().map { it.toObjectModel() }
            val locations = appDatabase.locationItemDao().getAll().map { it.toLocationModel() }
            val routes = appDatabase.routeItemDao().getAll().map { it.toRouteModel() }

            val locationById = locations.associateBy { it.id }
            val objectsByLocationId = objects.groupBy { it.location }
            val routeById = routes.associateBy { it.id }

            MuseumCache(
                locations = locations,
                objects = objects,
                routes = routes,
                locationById = locationById,
                objectsByLocationId = objectsByLocationId,
                routeById = routeById
            )
        }

        built.also { cache = it }
    }

    override suspend fun warmUp() { load() }

    override fun clearCache() { cache = null }
}