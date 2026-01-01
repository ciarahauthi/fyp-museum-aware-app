package com.stitchumsdev.fyp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.stitchumsdev.fyp.core.navigation.AppNavigation
import com.stitchumsdev.fyp.ui.theme.FypTheme

@Composable
fun AppContent() {
    FypTheme {
        val navController = rememberNavController()
        AppNavigation(navController)
    }
}