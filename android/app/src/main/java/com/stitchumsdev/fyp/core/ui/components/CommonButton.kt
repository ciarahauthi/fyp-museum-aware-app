package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color = fypColours.tertiaryBackground,
    contentColor: Color = fypColours.mainText
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_small)),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge
        )
    }
}