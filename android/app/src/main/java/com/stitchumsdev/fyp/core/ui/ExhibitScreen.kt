package com.stitchumsdev.fyp.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.components.TopBar
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun ExhibitScreen(
    navHostController: NavHostController
    // ToDo replace with api values
) {
    Scaffold(
        topBar = { TopBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val state = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state),
        ) {
            // Item image
            Image(
                painter = painterResource(R.drawable.img_rusty),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.heightIn(max = dimensionResource(R.dimen.image_x_large))
            )
            // Item details
            Surface (
                color = fypColours.mainBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_16))
                ) {
                    Text(
                        text = "Rusty",
                        style = Typography.headlineLarge,
                        color = fypColours.mainText
                    )
                    repeat(5) {
                        Text(
                            text = stringResource(R.string.long_string_placeholder),
                            style = Typography.bodyLarge,
                            color = fypColours.mainText
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExhibitScreenPreview() {
    FypTheme {
        ExhibitScreen(rememberNavController())
    }
}