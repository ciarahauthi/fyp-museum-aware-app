package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun CommonButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    selected: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_small)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) fypColours.tertiaryBackground else fypColours.secondaryBackground,
            contentColor = if (selected) fypColours.mainText else fypColours.secondaryText
        )
    ) {
        Text(
            text = text,
            style = Typography.labelLarge
        )
    }
}