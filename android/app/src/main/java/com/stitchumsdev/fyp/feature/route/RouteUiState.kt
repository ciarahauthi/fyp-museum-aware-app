package com.stitchumsdev.fyp.feature.route

import com.stitchumsdev.fyp.core.model.LocationModel

sealed interface RouteUiState {
    data object Loading : RouteUiState
    data object Error: RouteUiState // ToDo finish
    // Not routing state
    data class Default(
        val routes: List<List<LocationModel>> = emptyList(),
        val selectedStops: List<LocationModel> = emptyList()
    ) : RouteUiState
    // Routing state
    data class Routing(
        val stops: List<LocationModel>,
        val currentIndex: Int = 0,
        val currentLocation: LocationModel? = null // Where I am from scanning
    ) : RouteUiState {
        // Derive from stops list
        val currentTarget: LocationModel? get() = stops.getOrNull(currentIndex)
        val nextTarget: LocationModel? get() = stops.getOrNull(currentIndex + 1)
    }
}