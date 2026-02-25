package com.stitchumsdev.fyp.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun ExhibitInformationScreen(
    navHostController: NavHostController,
    exhibit: ObjectModel
) {
    InformationScreen(
        navHostController = navHostController,
        title = exhibit.title,
        image = R.drawable.img_rusty //ToDo Change
    ) {
        val childFriendly = if (exhibit.childFriendly) R.string.yes else R.string.no
        val likes = exhibit.likes
        val dislikes = exhibit.dislikes
        val total = likes + dislikes

        val likePercent = if (total == 0) 0 else (likes * 100) / total

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_8)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.child_friendly) + " ${stringResource(childFriendly)}",
                style = Typography.titleSmall,
                color = fypColours.secondaryText
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4)),
                verticalAlignment = Alignment.CenterVertically
            ){
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
        HorizontalDivider()

        Text(
            text = exhibit.description,
            style = Typography.bodyLarge,
            color = fypColours.mainText)
    }
}