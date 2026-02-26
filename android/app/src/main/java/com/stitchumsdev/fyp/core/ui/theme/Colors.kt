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
    val secondaryText: Color = Color.Unspecified,
    val mainBackground: Color = Color.Unspecified,
    val secondaryBackground: Color = Color.Unspecified,
    val tertiaryBackground: Color = Color.Unspecified,
    val navBar: Color = Color.Unspecified,
    val thumbsUp: Color = Color.Unspecified,
    val thumbsDown: Color = Color.Unspecified,
)

@Composable
fun getLightColors() = FypColours (
    mainText = colorResource(R.color.charcoal),
    secondaryText = colorResource(R.color.text_grey),
    mainBackground = colorResource(R.color.parchment),
    secondaryBackground = colorResource(R.color.bone),
    tertiaryBackground = colorResource(R.color.olive),
    navBar = colorResource(R.color.parchment),
    thumbsUp = colorResource(R.color.thumbs_up),
    thumbsDown = colorResource(R.color.thumbs_down)
)

@Composable
fun getDarkColors() = FypColours (
    mainText = colorResource(R.color.parchment),
    secondaryText = colorResource(R.color.text_grey),
    mainBackground = colorResource(R.color.dark),
    secondaryBackground = colorResource(R.color.charcoal),
    tertiaryBackground = colorResource(R.color.maroon),
    navBar = colorResource(R.color.dark),
    thumbsUp = colorResource(R.color.thumbs_up),
    thumbsDown = colorResource(R.color.thumbs_down)
)

val fypColours: FypColours
    @Composable @ReadOnlyComposable
    get() = LocalColors.current
val LocalColors = staticCompositionLocalOf { FypColours() }