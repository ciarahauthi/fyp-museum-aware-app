package com.stitchumsdev.fyp.feature.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar

@Composable
fun SearchScreen(
    navHostController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Text(
            text = "Hello SearchScreen!",
            modifier = Modifier.padding(innerPadding)
        )
    }
}