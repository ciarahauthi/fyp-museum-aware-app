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
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateHome: () -> Unit
) {

    LaunchedEffect(Unit) {
        delay(1500)
        navigateHome()
    }

    Scaffold { safePadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(safePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hello World")
        }
    }
}