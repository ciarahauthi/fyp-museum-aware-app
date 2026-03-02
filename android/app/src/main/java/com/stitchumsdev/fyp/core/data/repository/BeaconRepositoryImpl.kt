package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.BeaconEvent
import com.stitchumsdev.fyp.core.model.BeaconEventsRequest
import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.IBeaconData
import com.stitchumsdev.fyp.core.model.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.UUID

// This repository is for handling the caches for beacon packets.
// It sends to the API. It is used to find nearby objects
class BeaconRepositoryImpl(
    private val museumRepository: MuseumRepository,
    private val userRepository: UserRepository
) : BeaconRepository {
    private val maxCacheSize: Int = 10000
    // 10s
    private val updateNearbyCacheTime: Long = 10000L
    private val beaconCache = BeaconCache(maxCacheSize)
    private val nearbyObjectsCache = NearbyObjectsCache(updateNearbyCacheTime)
    private val mutex = Mutex()
    private val _nearbyObjects = MutableStateFlow<List<BeaconId>>(emptyList())
    override val nearbyObjects: StateFlow<List<BeaconId>> = _nearbyObjects.asStateFlow()

    // For deriving current location
    private val _currentLocationId = MutableStateFlow<Int?>(null)
    override val currentLocationId: StateFlow<Int?> = _currentLocationId.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationModel?>(null)
    override val currentLocation: StateFlow<LocationModel?> = _currentLocation.asStateFlow()

    private val sessionManager = SessionManager()

    @Volatile private var isFlushing = false

    override suspend fun onBeacon(beacon: IBeaconData) {
        val sessionId = sessionManager.sessionId()

        var shouldFlush = false // Boolean to trigger if cache is full

        mutex.withLock {
            val packet = BeaconInformation(
                sessionId = sessionId,
                beaconData = beacon
            )
            shouldFlush = beaconCache.addPacket(packet)

            nearbyObjectsCache.addPacket(beacon.toBeaconId())
            _nearbyObjects.value = nearbyObjectsCache.getObjects()
        }

        updateCurrentLocation()

        if (shouldFlush) {
            flushCacheToServer()
        }
    }

    override suspend fun uploadClearPackets() {
        flushCacheToServer()
    }

    override fun endSession() {
        sessionManager.reset()
    }

    private class BeaconCache(
        private val maxSize: Int
    ) {
        private val cache = mutableListOf<BeaconInformation>()

        // Add packet to the cache
        // Return true if cache should be emptied
        fun addPacket(packet: BeaconInformation): Boolean {
            cache.add(packet)
            return cache.size >= maxSize
        }

        // Sends a copy of the cache to be sent to the server.
        // Empties the cache afterwards
        fun drain(): List<BeaconInformation> {
            if (cache.isEmpty()) return emptyList()
            val copy = cache.toList()
            cache.clear()
            return copy
        }

        // Function to put the beacon packets back into the cache if POST to server fails
        fun requeue(packets: List<BeaconInformation>) {
            if (packets.isEmpty()) return

            // Put failed batch back at the front
            cache.addAll(0, packets)

            // If too big, drop newest overflow
            if (cache.size > maxSize) {
                cache.subList(maxSize, cache.size).clear()
            }
        }

        fun size(): Int = cache.size
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

    // Derives the current location from the nearby objects cache.
    // Majority vote = current location
    private suspend fun updateCurrentLocation() {
        val cache = museumRepository.load()
        val nearby = nearbyObjects.value

        val locationVotes: Map<Int, Int> =
            nearby.mapNotNull { beaconId ->
                val obj = cache.objectsByBeaconId[beaconId]
                obj?.location
            }.groupingBy { it }.eachCount()

        val bestLocationId = locationVotes.maxByOrNull { it.value }?.key

        if (bestLocationId != null) {
            _currentLocationId.value = bestLocationId
            _currentLocation.value = cache.locationById[bestLocationId]
        } else {
            Timber.d("!! No beacons. Last locationId=${_currentLocationId.value}")
        }

        _currentLocationId.value = bestLocationId
        _currentLocation.value = bestLocationId?.let { cache.locationById[it] }
    }

    private suspend fun flushCacheToServer() {
        if (isFlushing) return
        isFlushing = true

        var data: List<BeaconInformation> = emptyList()

        try {
            data = mutex.withLock { beaconCache.drain() }
            if (data.isEmpty()) return

            val sessionId = data.first().sessionId
            val body = BeaconEventsRequest(
                sessionId = sessionId,
                events = data.map { it.toBeaconEvent() }
            )

            userRepository.sendBeaconEvents(body)
            Timber.d("!! Uploaded ${data.size} beacon packets")

        } catch (e: Exception) {
            Timber.e(e, "!! Beacon upload failed, requeueing")

            if (data.isNotEmpty()) mutex.withLock { beaconCache.requeue(data) }

        } finally {
            isFlushing = false
        }
    }
}

// For sending to the server
data class BeaconInformation(
    val sessionId: String,
    val beaconData: IBeaconData,
    val recordedAt: Long = System.currentTimeMillis()
) {
    fun toBeaconEvent() = BeaconEvent(
        beaconUuid = beaconData.uuid,
        beaconMajor = beaconData.major,
        beaconMinor = beaconData.minor,
        rssi = beaconData.rssi,
        txPower = beaconData.txPower,
        recordedAt = recordedAt
    )
}
// Session manager generates a UUID per each session. It is reset upon closure of the app.
private class SessionManager {
    private var sessionId: String? = null

    fun sessionId(): String {
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString()
        }
        return sessionId!!
    }

    fun reset() {
        sessionId = null
    }
}