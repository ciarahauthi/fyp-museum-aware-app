package com.stitchumsdev.fyp.feature.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object Error: SplashUiState
    data object Done: SplashUiState
}