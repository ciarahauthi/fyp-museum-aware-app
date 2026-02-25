package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.ui.theme.fypColours


@Composable
fun ExhibitRow(
    obj: ObjectModel,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: (ObjectModel) -> Unit = {},

) {
    val title = obj.title.ifBlank { "Untitled" }
    val description = obj.category

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4))
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = fypColours.mainText
            )
            // Description
            Text(
                text = description,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = fypColours.secondaryText
            )
        }

        if (selectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect(obj) }
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_chevron),
                contentDescription = null,
                tint = fypColours.mainText,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
            )
        }

    }
    HorizontalDivider()
}