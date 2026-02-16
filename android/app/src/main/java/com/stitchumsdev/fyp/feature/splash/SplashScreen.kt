package com.stitchumsdev.fyp.feature.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    navigateHome: () -> Unit
) {
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Done) {
            navigateHome()
        }
    }

    when (uiState) {
        is SplashUiState.Loading -> SplashLoad()
        is SplashUiState.Error -> {} //ToDo
        is SplashUiState.Done -> SplashLoad()
    }
}

@Composable
fun SplashLoad() {
    Scaffold { safePadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(safePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Loading")
        }
    }
}