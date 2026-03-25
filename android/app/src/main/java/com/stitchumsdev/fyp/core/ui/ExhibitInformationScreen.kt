package com.stitchumsdev.fyp.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ExhibitModel
import com.stitchumsdev.fyp.core.ui.components.FeedbackBox
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun ExhibitInformationScreen(
    navHostController: NavHostController,
    exhibit: ExhibitModel,
    locationName: String? = null,
    onLocationClick: () -> Unit = {},
    onRate: (Int, Boolean) -> Unit
) {
    val likes = exhibit.likes
    val dislikes = exhibit.dislikes
    val total = likes + dislikes
    val likePercent = if (total == 0) 0 else (likes * 100) / total

    InformationScreen(
        navHostController = navHostController,
        title = exhibit.title,
        imageUrl = exhibit.imageUrl,
        titleTrailing = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_thumbs_up),
                    contentDescription = null,
                    tint = fypColours.secondaryText,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_x_small))
                )
                Text(
                    text = "$likePercent%",
                    style = Typography.titleSmall,
                    color = fypColours.secondaryText
                )
            }
        }
    ) {
        if (locationName != null) {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLocationClick() }
                    .padding(vertical = dimensionResource(R.dimen.padding_8)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.location)} $locationName",
                    style = Typography.titleSmall,
                    color = fypColours.secondaryText
                )
                Icon(
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = null,
                    tint = fypColours.secondaryText,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
                )
            }
        }

        AccessibilityTile(exhibit = exhibit)

        HorizontalDivider()

        Text(
            text = exhibit.description,
            style = Typography.bodyLarge,
            color = fypColours.mainText
        )

        if (exhibit.canRate) FeedbackBox(onRate = { rating -> onRate(exhibit.id, rating) })
    }
}

@Composable
private fun AccessibilityTile(exhibit: ExhibitModel) {
    var expanded by remember { mutableStateOf(false) }

    HorizontalDivider()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = dimensionResource(R.dimen.padding_8)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.accessibility_info),
            style = Typography.titleSmall,
            color = fypColours.secondaryText
        )
        Icon(
            painter = painterResource(
                if (expanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down
            ),
            contentDescription = null,
            tint = fypColours.secondaryText,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
        )
    }

    AnimatedVisibility(visible = expanded) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_8)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4))
        ) {
            AccessibilityRow(
                label = stringResource(R.string.child_friendly),
                value = if (exhibit.childFriendly) stringResource(R.string.yes) else stringResource(R.string.no)
            )
            AccessibilityRow(
                label = stringResource(R.string.is_loud),
                value = if (exhibit.isLoud) stringResource(R.string.yes) else stringResource(R.string.no)
            )
            AccessibilityRow(
                label = stringResource(R.string.is_crowded),
                value = if (exhibit.isCrowded) stringResource(R.string.yes) else stringResource(R.string.no)
            )
            AccessibilityRow(
                label = stringResource(R.string.is_dark),
                value = if (exhibit.isDark) stringResource(R.string.yes) else stringResource(R.string.no)
            )
        }
    }
}

@Composable
private fun AccessibilityRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = Typography.bodyMedium, color = fypColours.secondaryText)
        Text(text = value, style = Typography.bodyMedium, color = fypColours.secondaryText)
    }
}
