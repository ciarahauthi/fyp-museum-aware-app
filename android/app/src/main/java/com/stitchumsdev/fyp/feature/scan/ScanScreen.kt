package com.stitchumsdev.fyp.feature.scan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar

@Composable
fun ScanScreen(
    navHostController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Text(
            text = "Hello ScanScreen!",
            modifier = Modifier.padding(innerPadding)
        )
    }
}