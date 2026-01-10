package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.IBeaconData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

// This repository is for handling the caches for beacon packets.
// It sends to the API. It is used to find nearby objects
class BeaconRepositoryImpl(
) : BeaconRepository {
    private val maxCacheSize: Int = 10000
    // 10s
    private val updateNearbyCacheTime: Long = 10000L
    private val beaconCache = BeaconCache(maxCacheSize)
    private val nearbyObjectsCache = NearbyObjectsCache(updateNearbyCacheTime)
    private val mutex = Mutex()
    private val _nearbyObjects = MutableStateFlow<List<BeaconId>>(emptyList())
    override val nearbyObjects: StateFlow<List<BeaconId>> = _nearbyObjects.asStateFlow()

    override suspend fun onBeacon(beacon: IBeaconData) {
        mutex.withLock {
            beaconCache.addPacket(beacon)
            nearbyObjectsCache.addPacket(beacon.toBeaconId())
            _nearbyObjects.value = nearbyObjectsCache.getObjects()
        }
    }

    override suspend fun uploadClearPackets() {
        Timber.d("!! Uploaded packets to server")
        mutex.withLock {
            beaconCache.clearAndSend()
        }
    }

    private class BeaconCache(
        private val maxSize: Int
    ) {
        private val cache = mutableListOf<IBeaconData>()

        // Only add packet if cache isn't full
        fun addPacket(packet: IBeaconData) {
            if (cache.size >= maxSize) {
                clearAndSend()
            }
            cache.add(packet)
        }

        fun clearAndSend() {
            // ToDo time check & API call here
            cache.clear()
        }
    }

    // Using a map to only show the nearby unique objects
    // Since cache always changing, save current cache in a variable to be accessed by UI
    private class NearbyObjectsCache(
        private val updateTime: Long
    ) {
        private val cache = mutableMapOf<BeaconId, Long>()
        private var cacheCopy: List<BeaconId> = emptyList()

        fun addPacket(packet: BeaconId) {
            // Upsert beacon
            val currentTime = System.currentTimeMillis()

            cache[packet] = currentTime

            // Remove old packets
            removeOldObjects(currentTime)

            // Update cache copy
            cacheCopy = cache.keys.toList()
        }

        fun getObjects(): List<BeaconId> {
            val currentTime = System.currentTimeMillis()
            removeOldObjects(currentTime)

            return this.cacheCopy
        }

        private fun removeOldObjects(currentTime: Long) {
            val cutoffTime = currentTime - updateTime
            cache.entries.removeAll { it.value < cutoffTime }
        }
    }
}