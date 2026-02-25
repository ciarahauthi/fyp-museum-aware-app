package com.stitchumsdev.fyp.core.model

data class RouteModel(
    val id: Int,
    val name: String,
    val description: String,
    val nodes: List<LocationModel>,
    val stops: List<ObjectModel>
)