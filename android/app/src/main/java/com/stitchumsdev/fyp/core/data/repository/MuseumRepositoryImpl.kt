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
            val objects = appDatabase.exhibitItemDao().getAll().first().map { it.toObjectModel() }
            val locations = appDatabase.locationItemDao().getAll().first().map { it.toLocationModel() }

            val locationById = locations.associateBy { it.id }
            val objectsByLocationId = objects.groupBy { it.location }

            MuseumCache(
                locations = locations,
                objects = objects,
                locationById = locationById,
                objectsByLocationId = objectsByLocationId
            )
        }

        built.also { cache = it }
    }

    override suspend fun warmUp() { load() }

    override fun clearCache() { cache = null }
}