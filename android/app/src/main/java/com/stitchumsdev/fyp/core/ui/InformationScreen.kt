package com.stitchumsdev.fyp.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.SubcomposeAsyncImage
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
    titleTrailing: (@Composable () -> Unit)? = null,
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
                .verticalScroll(state),
        ) {
            // Item image
            val model: Any = imageUrl?.takeIf { it.isNotBlank() } ?: fallbackImage

            SubcomposeAsyncImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = dimensionResource(R.dimen.image_x_large)),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = dimensionResource(R.dimen.image_x_large)),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            )
            // Item details
            Surface(
                color = fypColours.mainBackground,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_8)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = Typography.headlineLarge,
                            color = fypColours.mainText,
                            modifier = Modifier.weight(1f)
                        )
                        titleTrailing?.invoke()
                    }
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