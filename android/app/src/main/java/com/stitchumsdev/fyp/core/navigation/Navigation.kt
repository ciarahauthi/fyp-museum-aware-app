package com.stitchumsdev.fyp.core.navigation

import RouteSelectionScreen
import SearchScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stitchumsdev.fyp.core.ui.ExhibitInformationScreen
import com.stitchumsdev.fyp.feature.home.HomeScreen
import com.stitchumsdev.fyp.feature.home.HomeViewModel
import com.stitchumsdev.fyp.feature.map.MapScreen
import com.stitchumsdev.fyp.feature.map.MapViewModel
import com.stitchumsdev.fyp.feature.route.RouteAction
import com.stitchumsdev.fyp.feature.route.RouteInformationScreen
import com.stitchumsdev.fyp.feature.route.RouteScreen
import com.stitchumsdev.fyp.feature.route.RouteUiState
import com.stitchumsdev.fyp.feature.route.RouteViewModel
import com.stitchumsdev.fyp.feature.scan.ScanScreen
import com.stitchumsdev.fyp.feature.scan.ScanViewModel
import com.stitchumsdev.fyp.feature.search.SearchUiState
import com.stitchumsdev.fyp.feature.search.SearchViewModel
import com.stitchumsdev.fyp.feature.splash.SplashScreen
import com.stitchumsdev.fyp.feature.splash.SplashViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
object Splash
@Serializable
object Home
@Serializable
object Map
@Serializable
object Search
@Serializable
object Scan
@Serializable
object Route
@Serializable
data class RouteInfo(val routeId: Int)
@Serializable
data class ExhibitInfo(val exhibitId: Int)
@Serializable
object RouteSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navHostController: NavHostController,
    scanViewModel: ScanViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    mapViewModel: MapViewModel = koinViewModel(),
    routeViewModel: RouteViewModel = koinViewModel(),
    splashViewModel: SplashViewModel = koinViewModel(),
    searchViewModel: SearchViewModel = koinViewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = Splash
    ) {
        composable<Splash> {
            val uiState = splashViewModel.uiState.collectAsState()
            SplashScreen(
                uiState = uiState.value,
                navigateHome = {navHostController.navigate(Home) })
        }
        composable<Home> {
            HomeScreen(
                navHostController = navHostController,
                onAction = { action -> homeViewModel.onAction(action) }
            )
        }
        composable<Map> {
            val uiState = mapViewModel.uiState.collectAsState()
            val routeUiState = routeViewModel.uiState.collectAsState()
            MapScreen(
                uiState = uiState.value,
                routeUiState = routeUiState.value,
                navHostController)
        }
        composable<Scan> {
            val uiState = scanViewModel.uiState.collectAsState()
            ScanScreen(
                navHostController = navHostController,
                uiState = uiState.value,
                onAction = { action ->
                scanViewModel.onAction(action) }
            )
        }
        composable<Search> {
            val uiState = searchViewModel.uiState.collectAsState()
            SearchScreen(
                navHostController = navHostController,
                uiState = uiState.value,
                onAction = { action -> searchViewModel.onAction(action) },
                onObjectClick = { obj ->
                    navHostController.navigate(ExhibitInfo(exhibitId = obj.id))
                }
            )
        }
        composable<Route> {
            val uiState = routeViewModel.uiState.collectAsState()
            RouteScreen(
                navHostController = navHostController,
                uiState = uiState.value,
                onAction = { action ->
                    routeViewModel.onAction(action) })
        }
        composable<RouteInfo> { backStackEntry ->
            val routeId = backStackEntry.arguments?.getInt("routeId") ?: return@composable

            LaunchedEffect(routeId) {
                routeViewModel.onAction(RouteAction.SelectRoute(routeId))
            }

            val routeInfo = routeViewModel.selectedRoute.collectAsState().value

            if (routeInfo != null) {
                RouteInformationScreen(
                    navHostController = navHostController,
                    routeInfo = routeInfo,
                    onObjectClick = { obj ->
                        navHostController.navigate(ExhibitInfo(exhibitId = obj.id))
                    },
                    onStartRoute = { route ->
                        routeViewModel.onAction(RouteAction.StartRouting(route))
                        navHostController.popBackStack()
                    }
                )
            } else {
                // ToDo Loading Screen
            }
        }

        composable<ExhibitInfo> { backStackEntry ->
            val exhibitId = backStackEntry.arguments?.getInt("exhibitId") ?: return@composable

            val searchState = searchViewModel.uiState.collectAsState().value
            val exhibit = (searchState as? SearchUiState.Default)
                ?.objects
                ?.firstOrNull { it.id == exhibitId }

            if (exhibit != null) {
                ExhibitInformationScreen(
                    navHostController = navHostController,
                    exhibit = exhibit
                )
            } else {
                // TODO loading
            }
        }

        composable<RouteSelection> {
            val searchUiState = searchViewModel.uiState.collectAsState()
            val routeUiState = routeViewModel.uiState.collectAsState()

            val selectedStops =
                (routeUiState.value as? RouteUiState.Default)
                    ?.selectedStops.orEmpty()

            val selectedIds = selectedStops.map { it.id }.toSet()

            RouteSelectionScreen(
                navHostController = navHostController,
                uiState = searchUiState.value,
                onSearchAction = { searchViewModel.onAction(it) },
                selectedStopsCount = selectedStops.size,
                canStart = selectedStops.isNotEmpty(),
                selectedIds = selectedIds,
                onClearStops = { routeViewModel.onAction(RouteAction.ClearStops) },
                onStart = {
                    routeViewModel.onAction(RouteAction.StartRouting(selectedStops))
                    navHostController.popBackStack()
                },
                onToggleSelect = { obj ->
                    routeViewModel.onAction(RouteAction.ToggleStopById(obj.location))
                }
            )
        }
    }
}