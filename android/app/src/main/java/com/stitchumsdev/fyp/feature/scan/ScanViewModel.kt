package com.stitchumsdev.fyp.feature.scan

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(
    private val beaconRepository: BeaconRepository,
    private val userRepository: UserRepository
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
                // Get the nearby objects, convert to string for get request
                val beacons = beaconRepository.nearbyObjects.value
                val sendData: List<String> = beacons.map { it.convertToQuery() }

                val response = userRepository.getObject(sendData)
                // Map response to object model for UI state
                val objects = response.map { it.toObjectModel() }
                _uiState.value =
                    if (response.isEmpty()) ScanUiState.NoContent
                    else ScanUiState.Success(objects)
            } catch (t: Throwable) {
                _uiState.value = ScanUiState.Error
            } finally {
                searching = false
            }
        }
    }
}