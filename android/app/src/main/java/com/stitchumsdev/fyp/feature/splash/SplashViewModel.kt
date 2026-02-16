package com.stitchumsdev.fyp.feature.splash

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SplashViewModel(
    private val userRepository: UserRepository,
    private val appDatabase: AppDatabase,
    museumRepository: MuseumRepository
): BaseViewModel<SplashScreenAction>() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    override fun onAction(action: SplashScreenAction) {
    }

    init {
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val exhibits = userRepository.getExhibits()
                    val locations = userRepository.getLocations()
                    val routes = userRepository.getRoutes()

                    withContext(Dispatchers.IO) {
                        appDatabase.exhibitItemDao().upsertAll(exhibits.map { it.toExhibitEntity() })
                        appDatabase.locationItemDao().upsertAll(locations.map { it.toLocationItemEntity() })
                        appDatabase.routeItemDao().upsertAll(routes.map { it.toRouteItemEntity() })
                    }

                    museumRepository.clearCache()
                    museumRepository.warmUp()

                    _uiState.value = SplashUiState.Done
                    Timber.d("!! Load Success")
                } catch (e: Exception) {
                    Timber.e(e, "!! Warm-up failed")
                    _uiState.value = SplashUiState.Error
                }
            }

        }
    }
}