package com.stitchumsdev.fyp.feature.route

import com.stitchumsdev.fyp.core.base.Action
import com.stitchumsdev.fyp.core.model.LocationModel

// Not named RouteScreenAction since the VM and Actions are shared between map and routing screens.
sealed class RouteAction : Action() {
    data class StartRouting(val route: List<LocationModel>) : RouteAction()
    data object EndRouting : RouteAction()
    // Next stop in the route
    data object NextStop : RouteAction()
    // For selecting custom routes.
    data class AddStop(val stop: LocationModel) : RouteAction()
    data class RemoveStop(val stop: LocationModel) : RouteAction()
    data class SelectRoute(val routeId: Int) : RouteAction()
}