package com.stitchumsdev.fyp.feature.route

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.model.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class RouteViewModel (
    private val museumRepository: MuseumRepository
) : BaseViewModel<RouteAction>() {
    private val _uiState = MutableStateFlow<RouteUiState>(RouteUiState.Loading)
    val uiState = _uiState.asStateFlow()

    override fun onAction(action: RouteAction) {
        when (action) {
            is RouteAction.AddStop -> TODO()
            RouteAction.EndRouting -> TODO()
            RouteAction.NextStop -> nextStop()
            is RouteAction.RemoveStop -> TODO()
            is RouteAction.StartRouting -> startRoute(action.route)
            is RouteAction.UpdateCurrent -> TODO()
        }
    }

    init{
        viewModelScope.launch {
            try {
                val cache = museumRepository.load()
                _uiState.value = RouteUiState.Default(routes = cache.routes)
                Timber.d("!! routes: ${uiState.value}")
            } catch (t: Throwable) {
                Timber.e("Error: $t")
                _uiState.value = RouteUiState.Error
            }
        }
    }

    private fun startRoute(route: List<LocationModel>) {
        if (route.isEmpty()) {
            _uiState.value = RouteUiState.Error
            return
        }
        viewModelScope.launch {
            _uiState.value = RouteUiState.Routing(
                stops = route,
                currentIndex = 0,
                currentLocation = route.first()
            )
        }
    }

    private fun nextStop() {
        val state = _uiState.value as? RouteUiState.Routing ?: return

        val arrived = state.currentTarget ?: return

        // Reached the end of the route
        if (state.currentIndex >= state.stops.lastIndex) {
            _uiState.value = RouteUiState.Default()
            return
        }

        _uiState.value = state.copy(
            currentLocation = arrived,
            currentIndex = state.currentIndex + 1
        )
    }
}