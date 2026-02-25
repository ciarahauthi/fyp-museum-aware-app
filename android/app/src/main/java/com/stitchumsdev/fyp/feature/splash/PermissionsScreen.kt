package com.stitchumsdev.fyp.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun PermissionScreen(
    onRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fypColours.mainBackground)
            .padding(dimensionResource(R.dimen.padding_32)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_16), Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.require_permissions),
            style = Typography.headlineMedium,
            color = fypColours.mainText
        )
        CommonButton(
            text = stringResource(R.string.grant_permissions),
            onClick = onRequest
        )
    }

}