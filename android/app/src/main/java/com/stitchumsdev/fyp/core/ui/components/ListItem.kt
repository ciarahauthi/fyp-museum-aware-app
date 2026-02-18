package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours


@Composable
fun ListItem(
    title: String,
    category: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Title
            Text(
                text = title,
                style = Typography.titleMedium,
                color = fypColours.mainText
            )
            // Category
            Text(
                text = category,
                style = Typography.labelLarge,
                color = fypColours.mainText
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron),
            contentDescription = null,
            tint = fypColours.mainText,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
        )
    }
    HorizontalDivider()
}