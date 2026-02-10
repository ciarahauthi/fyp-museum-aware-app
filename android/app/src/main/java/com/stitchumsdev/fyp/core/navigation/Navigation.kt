package com.stitchumsdev.fyp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stitchumsdev.fyp.core.ui.ExhibitScreen
import com.stitchumsdev.fyp.feature.home.HomeScreen
import com.stitchumsdev.fyp.feature.home.HomeViewModel
import com.stitchumsdev.fyp.feature.map.MapScreen
import com.stitchumsdev.fyp.feature.map.MapViewModel
import com.stitchumsdev.fyp.feature.scan.ScanScreen
import com.stitchumsdev.fyp.feature.scan.ScanViewModel
import com.stitchumsdev.fyp.feature.search.SearchScreen
import com.stitchumsdev.fyp.feature.splash.SplashScreen
import com.stitchumsdev.fyp.feature.test.TestScreen
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
object Exhibit
// ToDo: remove on deployment
@Serializable
object Test

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    testViewModel: TestScreenViewModel = koinViewModel(),
    scanViewModel: ScanViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    mapViewModel: MapViewModel = koinViewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreen(navigateHome = {navHostController.navigate(Home) })
        }
        composable<Home> {
            HomeScreen(
                navHostController = navHostController,
                onAction = { action -> homeViewModel.onAction(action) }
            )
        }
        composable<Map> {
            val uiState = mapViewModel.uiState.collectAsState()
            MapScreen(
                uiState = uiState.value,
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
            SearchScreen(navHostController)
        }
        composable<Exhibit> {
            ExhibitScreen(navHostController)
        }
        // ToDo: remove on deployment
        composable<Test> {
            TestScreen(navHostController, onAction = { action -> testViewModel.onAction(action) } )
        }
    }
}