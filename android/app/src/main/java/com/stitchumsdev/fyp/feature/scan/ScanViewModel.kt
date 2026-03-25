package com.stitchumsdev.fyp.feature.scan

import android.app.Application
import android.bluetooth.BluetoothManager
import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ScanViewModel(
    private val application: Application,
    private val beaconRepository: BeaconRepository,
    private val museumRepository: MuseumRepository
) : BaseViewModel<ScanScreenAction>() {
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var searching = false

    init {
        viewModelScope.launch {
            beaconRepository.nearbyObjects.collect { beacons ->
                val state = _uiState.value
                if (state is ScanUiState.BluetoothDisabled) return@collect
                try {
                    val cache = museumRepository.load()
                    val objects = beacons
                        .mapNotNull { cache.objectsByBeaconId[it] }
                        .distinctBy { it.id }
                    _uiState.value = ScanUiState.Success(objects)
                } catch (t: Throwable) {
                    _uiState.value = ScanUiState.Error
                    Timber.e("!! Error: $t")
                }
            }
        }
    }

    override fun onAction(action: ScanScreenAction) {
        when (action) {
            ScanScreenAction.GetNearbyObjects -> getObjects()
        }
    }

    private fun isBluetoothEnabled(): Boolean {
        val btManager = application.getSystemService(BluetoothManager::class.java)
        return btManager?.adapter?.isEnabled == true
    }

    // Manual refresh that also checks BT state
    private fun getObjects() {
        if (searching) return
        searching = true

        if (!isBluetoothEnabled()) {
            _uiState.value = ScanUiState.BluetoothDisabled
            searching = false
            return
        }

        viewModelScope.launch {
            try {
                val beacons = beaconRepository.nearbyObjects.value
                val cache = museumRepository.load()

                val objects = beacons
                    .mapNotNull { cache.objectsByBeaconId[it] }
                    .distinctBy { it.id }

                _uiState.value = ScanUiState.Success(objects)
            } catch (t: Throwable) {
                _uiState.value = ScanUiState.Error
                Timber.e("!! Error: $t")
            } finally {
                searching = false
            }
        }
    }
}