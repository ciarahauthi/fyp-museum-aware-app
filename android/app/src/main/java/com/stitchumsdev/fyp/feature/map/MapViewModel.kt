package com.stitchumsdev.fyp.feature.map

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel (
    private val museumRepository: MuseumRepository
) : BaseViewModel<MapScreenAction>() {
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState = _uiState.asStateFlow()

    override fun onAction(action: MapScreenAction) {}

    init {
        viewModelScope.launch {
            try {
                val cache = museumRepository.load()

                val locObjMap: Map<LocationModel, List<ObjectModel>> =
                    cache.locations.associateWith { loc ->
                        cache.objectsByLocationId[loc.id].orEmpty()
                    }

                _uiState.value =
                    if (locObjMap.isEmpty()) MapUiState.Error
                    else MapUiState.Success(locObjMap)

            } catch (t: Throwable) {
                Timber.e(t, "MapViewModel load failed")
                _uiState.value = MapUiState.Error
            }
        }
    }
}