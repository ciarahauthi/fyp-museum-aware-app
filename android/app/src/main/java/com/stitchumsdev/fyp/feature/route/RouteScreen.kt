package com.stitchumsdev.fyp.feature.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.SubcomposeAsyncImage
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.RouteModel
import com.stitchumsdev.fyp.core.navigation.RouteInfo
import com.stitchumsdev.fyp.core.navigation.RouteSelection
import com.stitchumsdev.fyp.core.ui.LoadingScreen
import com.stitchumsdev.fyp.core.ui.OfflineErrorScreen
import com.stitchumsdev.fyp.core.ui.components.AppInfoBox
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun RouteScreen(
    navHostController: NavHostController,
    uiState: RouteUiState,
    onAction: (RouteAction) -> Unit,
    onInfoBoxClick: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            RouteUiState.Error -> OfflineErrorScreen(onRetry = onRetry)
            RouteUiState.NoLocation -> NoLocationContent(onRetry = onRetry)
            RouteUiState.Loading -> LoadingScreen()
            is RouteUiState.Default -> RouteDefault(
                navHostController = navHostController,
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(fypColours.mainBackground)
            )
            is RouteUiState.Routing -> RouteRouting(
                uiState = uiState,
                onAction = onAction,
                onInfoBoxClick = onInfoBoxClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(fypColours.mainBackground)
            )
        }
    }
}

@Composable
fun NoLocationContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fypColours.mainBackground)
            .padding(dimensionResource(R.dimen.padding_32)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.spacing_16),
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_no_scan),
            contentDescription = null,
            tint = fypColours.mainText,
            modifier = Modifier.size(dimensionResource(R.dimen.image_large))
        )
        Text(
            text = stringResource(R.string.no_location),
            style = Typography.titleMedium,
            textAlign = TextAlign.Center,
            color = fypColours.mainText
        )
        Text(
            text = stringResource(R.string.no_location_desc),
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = fypColours.secondaryText
        )
        CommonButton(
            text = stringResource(R.string.try_again),
            onClick = onRetry
        )
    }
}

@Composable
fun RouteDefault(
    navHostController: NavHostController,
    uiState: RouteUiState.Default,
    modifier: Modifier = Modifier
) {
    val routes = uiState.routes
    // List of predetermined routes
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_8)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
    ) {
        Text(
            "Routes",
            style = Typography.headlineLarge,
            color = fypColours.mainText
        )
        HorizontalDivider()

        routes.forEach { route ->
            RouteItem(
                route,
                Modifier.clickable(
                    onClick = { navHostController.navigate(RouteInfo(route.id)) }
                )
            )
        }

        // Create routes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = fypColours.secondaryBackground,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium))
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.padding_8)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_route),
                    contentDescription = null,
                    tint = fypColours.mainText,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_medium)),
                )
                Text(
                    text = stringResource(R.string.want_make_route),
                    style = Typography.titleSmall.copy(
                        color = fypColours.mainText,
                        textAlign = TextAlign.Center
                    )
                )
                CommonButton(
                    text = stringResource(R.string.create_route),
                    onClick = { navHostController.navigate(RouteSelection) }
                )
            }
        }
    }
}

@Composable
fun RouteItem(
    route: RouteModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ){
        Box{
            val imageUrl = route.imageUrl?.takeIf { it.isNotBlank() }
            val imageModifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 200.dp)
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                )
            } else {
                Box(
                    modifier = imageModifier.background(fypColours.secondaryBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_no_image),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = fypColours.secondaryText
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_8))
                    .background(fypColours.mainBackground)
                    .align(Alignment.BottomStart)
            ){
                Text(
                    "${route.nodes.size} stops",
                    modifier = Modifier
                        .background(fypColours.mainBackground)
                        .padding(dimensionResource(R.dimen.padding_4)),
                    style = Typography.labelLarge,
                    color = fypColours.mainText
                )
            }
        }
        Text(
            text = route.name,
            style = Typography.titleMedium.copy(
                color = fypColours.mainText
            )
        )
        Text(
            text = route.description,
            style = Typography.bodyLarge.copy(
                color = fypColours.secondaryText
            )
        )
    }
}

@Composable
fun RouteRouting(
    uiState: RouteUiState.Routing,
    onAction: (RouteAction) -> Unit,
    onInfoBoxClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingStops = uiState.stops.drop(uiState.currentIndex)
    val stopNames = remainingStops.map { it.name }
    val currLoc = uiState.currentLocation

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_8)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
    ) {
        Text(
            text = stringResource(R.string.routing),
            style = Typography.titleLarge,
            color = fypColours.mainText
        )

        HorizontalDivider()

        Text(
            text = stringResource(R.string.current_loc) + " ${currLoc?.name}",
            style = Typography.bodyLarge,
            color = fypColours.mainText)

        Text(
            text = stringResource(R.string.next_stops) + stopNames,
            style = Typography.bodyLarge,
            color = fypColours.mainText)
        Text(
            text = stringResource(R.string.left_stops) + " ${stopNames.size}",
            style = Typography.bodyLarge,
            color = fypColours.mainText)

        CommonButton(
            text = stringResource(R.string.next_stop),
            onClick = { onAction(RouteAction.NextStop) },
        )

        CommonButton(
            text = stringResource(R.string.end_routing),
            onClick = { onAction(RouteAction.EndRouting) },
        )

        AppInfoBox(
            title = stringResource(R.string.routing_tip),
            clickable = true,
            modifier = Modifier.clickable{ onInfoBoxClick() }
        )
    }
}
