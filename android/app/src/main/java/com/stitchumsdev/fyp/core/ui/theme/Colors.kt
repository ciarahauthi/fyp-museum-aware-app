package com.stitchumsdev.fyp.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.stitchumsdev.fyp.R

@Immutable
data class FypColours(
    val mainText: Color = Color.Unspecified,
    val mainBackground: Color = Color.Unspecified,
    val navBar: Color = Color.Unspecified,
)

@Composable
fun getLightColors() = FypColours (
    mainText = colorResource(R.color.charcoal),
    mainBackground = colorResource(R.color.parchment),
    navBar = colorResource(R.color.parchment)
)

@Composable
fun getDarkColors() = FypColours (
    mainText = colorResource(R.color.parchment),
    mainBackground = colorResource(R.color.black),
    navBar = colorResource(R.color.charcoal)
)

val fypColours: FypColours
    @Composable @ReadOnlyComposable
    get() = LocalColors.current
val LocalColors = staticCompositionLocalOf { FypColours() }