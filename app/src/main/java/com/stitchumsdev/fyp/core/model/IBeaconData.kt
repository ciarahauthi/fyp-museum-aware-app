package com.stitchumsdev.fyp.core.model

data class IBeaconData(
    val uuid: String,
    val major: Int,
    val minor: Int,
    val txPower: Int,
    val rssi: Int
)