package com.stitchumsdev.fyp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stitchumsdev.fyp.feature.home.HomeScreen
import com.stitchumsdev.fyp.feature.map.MapScreen
import com.stitchumsdev.fyp.feature.scan.ScanScreen
import com.stitchumsdev.fyp.feature.search.SearchScreen
import com.stitchumsdev.fyp.feature.splash.SplashScreen
import kotlinx.serialization.Serializable

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

@Composable
fun AppNavigation(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreen(navigateHome = {navHostController.navigate(Home) })
        }
        composable<Home> {
            HomeScreen(navHostController)
        }
        composable<Map> {
            MapScreen(navHostController)
        }
        composable<Search> {
            SearchScreen(navHostController)
        }
        composable<Scan> {
            ScanScreen(navHostController)
        }
    }
}