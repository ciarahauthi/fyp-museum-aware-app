package com.stitchumsdev.fyp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun OfflineErrorScreen(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fypColours.mainBackground)
            .padding(dimensionResource(R.dimen.padding_8)),
        verticalArrangement = Arrangement.spacedBy(
            space = dimensionResource(R.dimen.spacing_16),
            alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_offline),
            contentDescription = null,
            tint = fypColours.mainText,
            modifier = Modifier.size(dimensionResource(R.dimen.image_large))
        )
        Text(
            text = stringResource(R.string.oops),
            style = Typography.titleSmall,
            textAlign = TextAlign.Center)
        Text(
            text = stringResource(R.string.internet_try_again),
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        CommonButton(
            stringResource(R.string.try_again),
            onClick = onRetry)
    }
}

@Preview
@Composable
fun OfflineErrorScreenPreview() {
    FypTheme {
        OfflineErrorScreen {}
    }
}