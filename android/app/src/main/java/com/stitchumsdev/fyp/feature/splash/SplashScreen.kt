package com.stitchumsdev.fyp.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.stitchumsdev.fyp.core.ui.LoadingScreen
import com.stitchumsdev.fyp.core.ui.OfflineErrorScreen

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    navigateHome: () -> Unit,
    onRetry: () -> Unit
) {
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Done) {
            navigateHome()
        }
    }

    when (uiState) {
        is SplashUiState.Loading, SplashUiState.Done -> LoadingScreen()
        is SplashUiState.Error -> OfflineErrorScreen( onRetry =  onRetry )
    }
}
