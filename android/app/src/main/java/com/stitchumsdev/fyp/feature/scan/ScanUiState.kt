package com.stitchumsdev.fyp.feature.scan

import com.stitchumsdev.fyp.core.model.ExhibitModel

sealed interface ScanUiState {
    data object Loading : ScanUiState
    data object Error: ScanUiState
    data object NoContent: ScanUiState
    data object BluetoothDisabled : ScanUiState
    data class Success(
        val objects: List<ExhibitModel>
    ) : ScanUiState
}