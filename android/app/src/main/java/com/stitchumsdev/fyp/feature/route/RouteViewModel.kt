package com.stitchumsdev.fyp.feature.route

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import com.stitchumsdev.fyp.core.model.ExhibitModel
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

    private var stopIndex: Int? = null // To stop nextStop() from triggering multiple times while detecting current location == next stop
    private var pendingRoute: List<LocationModel>? = null // Stored so retry can re-attempt the same request

    override fun onAction(action: RouteAction) {
        when (action) {
            RouteAction.EndRouting -> endRouting()
            RouteAction.NextStop -> nextStop()
            RouteAction.RetryStart -> pendingRoute?.let { startRoute(it) }
            RouteAction.ClearStops -> clearStops()
            is RouteAction.ToggleExhibit -> toggleExhibit(action.exhibit)
            is RouteAction.StartRouting -> startRoute(action.route)
            is RouteAction.GetRoutingFromExhibits -> getRouteFromExhibits(action.exhibits)
            is RouteAction.SelectRoute -> getSelectedRoute(action.routeId)
            is RouteAction.SelectCurrentLocation -> startRouteWithLocation(action.location)
        }
    }

    init{
        // Track current location
        viewModelScope.launch {
            beaconRepository.currentLocation.collect { location ->
                Timber.d("!! Location $location")

                val state = _uiState.value
                if (state is RouteUiState.Routing) {
                    val updated = state.copy(currentLocation = location)
                    _uiState.value = updated

                    val target = updated.nextTarget
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

    fun loadRoutes() {
        viewModelScope.launch {
            try {
                val cache = museumRepository.load()

                val current = _uiState.value
                _uiState.value = when (current) {
                    is RouteUiState.Routing -> current
                    else -> RouteUiState.Default(routes = cache.routes)
                }
            } catch (t: Throwable) {
                Timber.e(t, "!! Failed to load routes")
                _uiState.value = RouteUiState.Error
            }
        }
    }

    private fun startRoute(route: List<LocationModel>) {
        if (route.isEmpty()) {
            _uiState.value = RouteUiState.Error
            Timber.d("!! Error: No route")
            return
        }

        pendingRoute = route

        viewModelScope.launch {
            try {
                val cache = museumRepository.load()
                val currentLocation = beaconRepository.currentLocation.value

                if (currentLocation == null) {
                    // No location detected — ask user to pick their current room
                    _uiState.value = RouteUiState.SelectLocation(
                        pendingRoute = route,
                        locations = cache.locations
                    )
                    pendingRoute = null
                    return@launch
                }

                // Location known
                val response = userRepository.getRoute(
                    current = currentLocation.id,
                    targets = route.map { it.id }
                )
                val pathStops = response.path.mapNotNull { cache.locationById[it] }

                val routingState = RouteUiState.Routing(
                    stops = pathStops,
                    currentIndex = 0,
                    currentLocation = currentLocation,
                    estimatedMinutes = response.estimatedMinutes
                )
                _uiState.value = routingState
                pendingRoute = null

                if (routingState.nextTarget?.id == currentLocation.id) {
                    stopIndex = 0
                    nextStop()
                }
            } catch (e: Exception) {
                Timber.e(e, "!! Failed to start route")
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

    private fun toggleExhibit(exhibit: ExhibitModel) {
        val state = _uiState.value as? RouteUiState.Default ?: return
        val exists = state.selectedExhibits.any { it.id == exhibit.id }
        _uiState.value = if (exists) {
            state.copy(selectedExhibits = state.selectedExhibits.filterNot { it.id == exhibit.id })
        } else {
            state.copy(selectedExhibits = state.selectedExhibits + exhibit)
        }
    }

    private fun clearStops() {
        val state = _uiState.value as? RouteUiState.Default ?: return
        _uiState.value = state.copy(selectedExhibits = emptyList())
    }

    // Function that gets unique location IDs from selected exhibits then calls for a custom route from the server
    private fun getRouteFromExhibits(exhibits: List<ExhibitModel>) {
        viewModelScope.launch {
            val cache = museumRepository.load()
            val uniqueLocations = exhibits
                .map { it.location }
                .distinct()
                .mapNotNull { cache.locationById[it] }
            startRoute(uniqueLocations)
        }
    }

    private fun startRouteWithLocation(selectedLocation: LocationModel) {
        val state = _uiState.value as? RouteUiState.SelectLocation ?: return
        val route = state.pendingRoute

        viewModelScope.launch {
            try {
                val cache = museumRepository.load()
                val response = userRepository.getRoute(
                    current = selectedLocation.id,
                    targets = route.map { it.id }
                )
                val pathStops = response.path.mapNotNull { cache.locationById[it] }

                if (pathStops.isEmpty()) {
                    _uiState.value = RouteUiState.Error
                    return@launch
                }

                val routingState = RouteUiState.Routing(
                    stops = pathStops,
                    currentIndex = 0,
                    currentLocation = beaconRepository.currentLocation.value,
                    estimatedMinutes = response.estimatedMinutes
                )
                _uiState.value = routingState
            } catch (e: Exception) {
                Timber.e(e, "!! Failed to start route with selected location")
                _uiState.value = RouteUiState.Error
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
                    selectedExhibits = emptyList()
                )
            } catch (t: Throwable) {
                Timber.e(t, "!! Failed to end routing")
                _uiState.value = RouteUiState.Error
            }
        }
    }
}