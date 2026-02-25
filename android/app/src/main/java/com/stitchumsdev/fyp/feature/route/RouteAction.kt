package com.stitchumsdev.fyp.feature.route

import com.stitchumsdev.fyp.core.base.Action
import com.stitchumsdev.fyp.core.model.LocationModel

// Not named RouteScreenAction since the VM and Actions are shared between map and routing screens.
sealed class RouteAction : Action() {
    data class StartRouting(val route: List<LocationModel>) : RouteAction()
    data object EndRouting : RouteAction()
    // Next stop in the route
    data object NextStop : RouteAction()
    // For selecting custom routes
    data object ClearStops : RouteAction()
    data class ToggleStop(val stop: LocationModel) : RouteAction()
    data class ToggleStopById(val locationId: Int) : RouteAction()

    data class SelectRoute(val routeId: Int) : RouteAction()
}