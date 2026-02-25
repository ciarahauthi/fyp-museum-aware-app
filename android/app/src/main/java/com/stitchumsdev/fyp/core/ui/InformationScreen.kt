package com.stitchumsdev.fyp.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.components.TopBar
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun InformationScreen(
    navHostController: NavHostController,
    title: String,
    imageUrl: String?,
    @DrawableRes fallbackImage: Int = R.drawable.ic_no_image,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val state = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fypColours.mainBackground)
                .padding(innerPadding)
        ) {
            // Item image
            val model: Any = imageUrl?.takeIf { it.isNotBlank() } ?: fallbackImage

            AsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.heightIn(max = dimensionResource(R.dimen.image_x_large))
            )
            // Item details
            Surface (
                color = fypColours.mainBackground,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_16))
                        .verticalScroll(state),
                ) {
                    Text(
                        text = title,
                        style = Typography.headlineLarge,
                        color = fypColours.mainText
                    )
                    // Item / Route content
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
fun ExhibitScreenPreview() {
    FypTheme {
        InformationScreen(
            navHostController = rememberNavController(),
            title = "Hello World",
            imageUrl = null,
            fallbackImage = R.drawable.img_rusty,
            content = {}
        )
    }
}