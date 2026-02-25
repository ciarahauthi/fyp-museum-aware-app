package com.stitchumsdev.fyp.feature.scan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.ui.OfflineScreen
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours
import timber.log.Timber

@Composable
fun ScanScreen(
    navHostController: NavHostController,
    uiState: ScanUiState,
    onAction: (ScanScreenAction) -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fypColours.mainBackground)
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_8))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Search nearby",
                    style = Typography.headlineMedium,
                    color = fypColours.mainText
                )

                if (uiState is ScanUiState.Success) {
                    IconButton(onClick = { onAction(ScanScreenAction.GetNearbyObjects) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = null,
                            tint = fypColours.mainText,
                            modifier = Modifier.size(dimensionResource(R.dimen.image_small))
                        )
                    }
                }
            }

            when (uiState) {
                ScanUiState.Error -> {
                    Timber.d("!! Error state")
                    OfflineScreen { }
                }
                ScanUiState.Loading -> {
                    Timber.d("!! Loading state")
                    Loading()
                }
                ScanUiState.NoContent -> {
                    Timber.d("!! No Content state")
                    NoContent( onAction = { action -> onAction(action) } )
                }
                is ScanUiState.Success -> Content(uiState.objects, onAction = { action -> onAction(action) })
            }
        }
    }
}

@Composable
fun ScanScreenButton(
    text: String,
    onAction: (ScanScreenAction) -> Unit
) {
    Button(
        onClick = { onAction(ScanScreenAction.GetNearbyObjects) }
    ) {
        Text(
            text = text,
            style = Typography.bodyLarge.copy(
                color = fypColours.mainText
            )
        )
    }
}
@Composable
fun NoContent(
    onAction: (ScanScreenAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_32)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.spacing_16),
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_start_scan),
            contentDescription = null,
            tint = fypColours.mainText,
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_large))
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.scan),
                style = Typography.titleMedium,
                textAlign = TextAlign.Center,
                color = fypColours.mainText
            )
            ScanScreenButton(
                stringResource(R.string.button_scan),
                onAction = onAction
            )
        }
    }
}

@Composable
fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_16)))
        Text("Loading ... ", style = Typography.bodyLarge)
    }
}

@Composable
fun Content(
    list: List<ObjectModel>,
    onAction: (ScanScreenAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        list.forEach { item ->
            HorizontalDivider()
            ListItem(item)
        }
    }
}

@Composable
fun ListItem(
    item: ObjectModel,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        // ToDo put image here

        Column {
            // Title
            Text(
                item.title,
                textAlign = TextAlign.Center,
                style = Typography.titleMedium
            )
            // Category
            Text(
                item.category,
                textAlign = TextAlign.Center,
                style = Typography.bodyLarge
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_chevron),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.image_small))
        )
    }
}