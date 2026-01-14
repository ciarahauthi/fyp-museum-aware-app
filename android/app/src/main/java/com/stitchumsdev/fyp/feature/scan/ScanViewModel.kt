package com.stitchumsdev.fyp.feature.scan

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ScanViewModel(
    private val beaconRepository: BeaconRepository,
    private val appDatabase: AppDatabase
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


                val all = mutableListOf<ExhibitItemEntity>()

                for (b in beacons) {
                    val matches = appDatabase.exhibitItemDao()
                        .getByBeaconInfo(b.uuid, b.major, b.minor)
                        .first()
                    all += matches
                }

                val objects = all
                    .distinctBy { it.id }
                    .map { it.toObjectModel() }

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