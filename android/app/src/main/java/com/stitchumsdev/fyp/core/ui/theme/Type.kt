package com.stitchumsdev.fyp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.SemiBold,
        fontSize = 56.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Dongle,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)