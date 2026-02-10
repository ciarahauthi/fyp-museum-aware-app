package com.stitchumsdev.fyp.feature.map

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel (
    private val appDatabase: AppDatabase
) : BaseViewModel<MapScreenAction>() {
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState = _uiState.asStateFlow()

    override fun onAction(action: MapScreenAction) {}

    init {
        viewModelScope.launch {
            try {
                val objectModels = appDatabase.exhibitItemDao()
                    .getAll()
                    .first()
                    .map { it.toObjectModel() }
                val locations = appDatabase.locationItemDao()
                    .getAll()
                    .first()
                    .map { it.toLocationModel() }

                val objectsByLoc: Map<Int, List<ObjectModel>> =
                    objectModels.groupBy { it.location }

                val locObjMap: Map<LocationModel, List<ObjectModel>> =
                    locations.associateWith { loc ->
                        objectsByLoc[loc.id].orEmpty()
                    }

                _uiState.value =
                    if (locObjMap.isEmpty()) MapUiState.Error
                    else MapUiState.Success(locObjMap)

            } catch (t: Throwable) {
                Timber.d("Error: $t")
            }
        }
    }
}