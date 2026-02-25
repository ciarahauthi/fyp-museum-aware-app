package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun CommonTextButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = fypColours.mainText,
            disabledContentColor = fypColours.secondaryText
        )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge,
        )
    }
}