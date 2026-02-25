package com.stitchumsdev.fyp.feature.route

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.RouteModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class RouteViewModel (
    private val museumRepository: MuseumRepository,
    private val beaconRepository: BeaconRepository,
    private val userRepository: UserRepository
) : BaseViewModel<RouteAction>() {
    private val _uiState = MutableStateFlow<RouteUiState>(RouteUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedRoute = MutableStateFlow<RouteModel?>(null)
    val selectedRoute = _selectedRoute.asStateFlow()

    private var stopIndex: Int? = null // To stop nextStop() form triggering multiple times while detecting current location == next stop

    override fun onAction(action: RouteAction) {
        when (action) {
            RouteAction.EndRouting -> endRouting()
            RouteAction.NextStop -> nextStop()
            is RouteAction.ToggleStop -> toggleStop(action.stop)
            RouteAction.ClearStops -> clearStops()
            is RouteAction.StartRouting -> startRoute(action.route)
            is RouteAction.SelectRoute -> getSelectedRoute(action.routeId)
            is RouteAction.ToggleStopById -> toggleStopById(action.locationId)
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

        // Track current location
        viewModelScope.launch {
            beaconRepository.currentLocation.collect { location ->
                Timber.d("!! Location $location")

                val state = _uiState.value
                if (state is RouteUiState.Routing) {
                    val updated = state.copy(currentLocation = location)
                    _uiState.value = updated

                    val target = updated.currentTarget
                    if (location != null && target != null && location.id == target.id) {
                        if (stopIndex != updated.currentIndex) {
                            stopIndex = updated.currentIndex
                            nextStop()
                        }
                    } else {
                        stopIndex = null
                    }
                }
            }
        }
    }

    private fun startRoute(route: List<LocationModel>) {
        if (route.isEmpty()) {
            _uiState.value = RouteUiState.Error
            return
        }

        viewModelScope.launch {
            try {
                val cache = museumRepository.load()

                val currentLocation = beaconRepository.currentLocation.value
                    ?: run {
                        _uiState.value = RouteUiState.Error
                        return@launch
                    }

                val targetIds: List<Int> = route.map { it.id }

                val pathIds: List<Int> = userRepository.getRoute(
                    current = currentLocation.id,
                    targets = targetIds
                )

                val pathStops: List<LocationModel> = pathIds.mapNotNull { id ->
                    cache.locationById[id]
                }

                val routingState = RouteUiState.Routing(
                    stops = pathStops,
                    currentIndex = 0,
                    currentLocation = currentLocation
                )
                _uiState.value = routingState

                // Trigger if starting position == start of route
                if (routingState.currentTarget?.id == currentLocation.id) {
                    stopIndex = 0
                    nextStop()
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to start route")
                _uiState.value = RouteUiState.Error
            }
        }
    }

    private fun nextStop() {
        val state = _uiState.value as? RouteUiState.Routing ?: return

        if (state.currentIndex >= state.stops.lastIndex) {
            viewModelScope.launch {
                val cache = museumRepository.load()
                _uiState.value = RouteUiState.Default(routes = cache.routes)
            }
            return
        }

        _uiState.value = state.copy(
            currentIndex = state.currentIndex + 1
        )

        stopIndex = null
    }
    private fun getSelectedRoute(routeId: Int) {
        viewModelScope.launch {
            val cache = museumRepository.load()
            _selectedRoute.value = cache.routeById[routeId]
        }
    }

    private fun toggleStop(stop: LocationModel) {
        val state = _uiState.value as? RouteUiState.Default ?: return
        val exists = state.selectedStops.any { it.id == stop.id }
        _uiState.value = if (exists) {
            state.copy(selectedStops = state.selectedStops.filterNot { it.id == stop.id })
        } else {
            state.copy(selectedStops = state.selectedStops + stop)
        }
    }

    private fun clearStops() {
        val state = _uiState.value as? RouteUiState.Default ?: return
        _uiState.value = state.copy(selectedStops = emptyList())
    }

    private fun toggleStopById(locationId: Int) {
        val state = _uiState.value as? RouteUiState.Default ?: return
        viewModelScope.launch {
            val cache = museumRepository.load()
            val stop = cache.locationById[locationId] ?: return@launch

            val exists = state.selectedStops.any { it.id == stop.id }
            _uiState.value = if (exists) {
                state.copy(selectedStops = state.selectedStops.filterNot { it.id == stop.id })
            } else {
                state.copy(selectedStops = state.selectedStops + stop)
            }
        }
    }

    private fun endRouting() {
        stopIndex = null

        viewModelScope.launch {
            try {
                val cache = museumRepository.load()
                _uiState.value = RouteUiState.Default(
                    routes = cache.routes,
                    selectedStops = emptyList()
                )
            } catch (t: Throwable) {
                Timber.e(t, "Failed to end routing")
                _uiState.value = RouteUiState.Error
            }
        }
    }
}