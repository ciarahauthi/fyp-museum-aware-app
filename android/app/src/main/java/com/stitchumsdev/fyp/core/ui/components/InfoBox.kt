package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun AppInfoBox(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_small)),
        colors = CardDefaults.cardColors(
            containerColor = fypColours.secondaryBackground,
            contentColor = fypColours.mainText,
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_8)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_16))
        ) {
            Icon(
                painterResource(R.drawable.ic_information),
                contentDescription = null,
                tint = fypColours.mainText,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
            )

            Column {
                Text(
                    text = title,
                    style = Typography.titleSmall,
                    color = fypColours.mainText
                )

                description?.let {
                    Text(
                        text = title,
                        style = Typography.bodyLarge,
                        color = fypColours.mainText
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AppInfoBoxPreview() {
    FypTheme {
        AppInfoBox(
            "Hello World!",
            description = "Some description here!"
        )
    }
}