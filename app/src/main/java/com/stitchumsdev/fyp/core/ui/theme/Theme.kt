package com.stitchumsdev.fyp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun FypTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colours = if (darkTheme) getDarkColors() else getLightColors()

    CompositionLocalProvider(
        LocalColors provides colours
    ) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}