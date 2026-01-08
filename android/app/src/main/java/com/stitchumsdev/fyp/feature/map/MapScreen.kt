package com.stitchumsdev.fyp.feature.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar

@Composable
fun MapScreen(
    navHostController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Android view as library is Views only
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                SubsamplingScaleImageView(context).apply {
                    setImage(ImageSource.resource(R.drawable.img_ground_floor))
                }
            }
        )
    }
}