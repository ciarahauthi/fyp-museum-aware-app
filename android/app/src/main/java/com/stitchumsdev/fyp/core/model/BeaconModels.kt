package com.stitchumsdev.fyp.core.model

// Used to store data that will be sent to the server
data class IBeaconData(
    val uuid: String,
    val major: Int,
    val minor: Int,
    val txPower: Int,
    val rssi: Int
) {
    fun toBeaconId(): BeaconId =
        BeaconId(
            uuid = this.uuid,
            major = this.major,
            minor = this.minor
        )
}

// Used for the items nearby screen
data class BeaconId(
    val uuid: String,
    val major: Int,
    val minor: Int
) {
    fun convertToQuery(): String {
        return "${this.uuid}:${this.major}:${this.minor}"
    }
}