package com.stitchumsdev.fyp.feature.scan

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(
    private val beaconRepository: BeaconRepository
) : BaseViewModel<ScanScreenAction>() {
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.NoContent)
    val uiState = _uiState.asStateFlow()
    private var searching = false

    override fun onAction(action: ScanScreenAction) {
        when (action) {
            ScanScreenAction.GetNearbyObjects -> getObjects()
        }
    }

    // Avoid multiple searches
    private fun getObjects() {
        if (searching) return
        searching = true

        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading

            try {
                val objects = beaconRepository.nearbyObjects.value
                _uiState.value =
                    if (objects.isEmpty()) ScanUiState.NoContent
                    else ScanUiState.Success(objects)
            } catch (t: Throwable) {
                _uiState.value = ScanUiState.Error
            } finally {
                searching = false
            }
        }
    }
}