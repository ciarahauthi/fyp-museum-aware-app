package com.stitchumsdev.fyp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stitchumsdev.fyp.feature.home.HomeScreen
import com.stitchumsdev.fyp.feature.home.HomeViewModel
import com.stitchumsdev.fyp.feature.map.MapScreen
import com.stitchumsdev.fyp.feature.map.MapViewModel
import com.stitchumsdev.fyp.feature.route.RouteAction
import com.stitchumsdev.fyp.feature.route.RouteInformationScreen
import com.stitchumsdev.fyp.feature.route.RouteScreen
import com.stitchumsdev.fyp.feature.route.RouteViewModel
import com.stitchumsdev.fyp.feature.scan.ScanScreen
import com.stitchumsdev.fyp.feature.scan.ScanViewModel
import com.stitchumsdev.fyp.feature.search.SearchScreen
import com.stitchumsdev.fyp.feature.splash.SplashScreen
import com.stitchumsdev.fyp.feature.splash.SplashViewModel
import com.stitchumsdev.fyp.feature.test.TestScreenViewModel
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
// ToDo: remove on deployment
@Serializable
object Test

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    testViewModel: TestScreenViewModel = koinViewModel(),
    scanViewModel: ScanViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    mapViewModel: MapViewModel = koinViewModel(),
    routeViewModel: RouteViewModel = koinViewModel(),
    splashViewModel: SplashViewModel = koinViewModel()
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
            SearchScreen(
                navHostController = navHostController)
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
                    onStartRoute = { route ->
                        routeViewModel.onAction(RouteAction.StartRouting(route))
                        navHostController.popBackStack()
                    }
                )
            } else {
                // ToDo Loading Screen
            }
        }
    }
}