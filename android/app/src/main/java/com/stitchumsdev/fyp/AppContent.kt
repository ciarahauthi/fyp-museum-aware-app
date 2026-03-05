package com.stitchumsdev.fyp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.stitchumsdev.fyp.core.navigation.AppNavigation

@Composable
fun AppContent() {
    val navController = rememberNavController()
    AppNavigation(navController)
}