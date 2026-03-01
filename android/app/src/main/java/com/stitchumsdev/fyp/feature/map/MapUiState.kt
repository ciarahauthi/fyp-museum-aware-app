package com.stitchumsdev.fyp.feature.map

import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ExhibitModel

sealed interface MapUiState {
    data object Loading : MapUiState
    data object Error: MapUiState
    data class Success(
        val locations: Map<LocationModel, List<ExhibitModel>>,
        val currentLocation: LocationModel? = null
    ) : MapUiState
}