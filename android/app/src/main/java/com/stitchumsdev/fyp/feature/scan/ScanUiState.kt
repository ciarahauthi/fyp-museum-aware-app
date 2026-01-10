package com.stitchumsdev.fyp.feature.scan

import com.stitchumsdev.fyp.core.model.BeaconId
import com.stitchumsdev.fyp.core.model.ObjectModel

sealed interface ScanUiState {
    data object Loading : ScanUiState
    data object Error: ScanUiState
    data object NoContent: ScanUiState
    data class Success(
        val objects: List<ObjectModel>
    ) : ScanUiState
}