package com.stitchumsdev.fyp.feature.map

import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel

sealed interface MapUiState {
    data object Loading : MapUiState
    data object Error: MapUiState
    data class Success(
        val locations: Map<LocationModel, List<ObjectModel>>,
        val currentLocation: LocationModel? = null
    ) : MapUiState
}