package com.stitchumsdev.fyp.core.data.repository

import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.IBeaconData
import com.stitchumsdev.fyp.core.model.LocationModel
import kotlinx.coroutines.flow.StateFlow

interface BeaconRepository {

    // Provide the nearby objects
    val nearbyObjects: StateFlow<List<BeaconId>>
    val currentLocation: StateFlow<LocationModel?>
    val currentLocationId: StateFlow<Int?>

    suspend fun onBeacon(beacon: IBeaconData)
    // Function to send to server & clear cache on send
    suspend fun uploadClearPackets()
}