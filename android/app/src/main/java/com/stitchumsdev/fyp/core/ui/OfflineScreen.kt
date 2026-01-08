package com.stitchumsdev.fyp.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
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
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography

@Composable
fun OfflineScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.padding_32)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.spacing_16),
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_offline),
                contentDescription = null,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_large))
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.oops),
                    style = Typography.headlineLarge
                )
                Text(
                    text = stringResource(R.string.internet_try_again),
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun OfflineScreenPreview() {
    FypTheme {
        OfflineScreen()
    }
}