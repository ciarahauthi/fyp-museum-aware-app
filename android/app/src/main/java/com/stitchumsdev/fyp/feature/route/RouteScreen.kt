package com.stitchumsdev.fyp.feature.route

import androidx.compose.foundation.Image
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
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.RouteModel
import com.stitchumsdev.fyp.core.navigation.RouteInfo
import com.stitchumsdev.fyp.core.navigation.RouteSelection
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun RouteScreen(
    navHostController: NavHostController,
    uiState: RouteUiState,
    onAction: (RouteAction) -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(fypColours.mainBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                RouteUiState.Error -> {}
                RouteUiState.Loading -> {}
                is RouteUiState.Default -> RouteDefault(
                    navHostController = navHostController,
                    uiState = uiState,
                )
                is RouteUiState.Routing -> RouteRouting(
                    uiState,
                    onAction = onAction)
            }
        }
    }
}

@Composable
fun RouteDefault(
    navHostController: NavHostController,
    uiState: RouteUiState.Default,
) {
    val routes = uiState.routes
    // List of predetermined routes
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_8)),
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
                    style = Typography.titleMedium.copy(
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
            Image(
                painter = painterResource(R.drawable.img_rusty),
                contentDescription = null,
                modifier = Modifier.heightIn(max = 200.dp),
                contentScale = ContentScale.Crop
            )
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
    onAction: (RouteAction) -> Unit
) {
    val stopNames = uiState.stops.map { it.name }
    val currLoc = uiState.currentLocation

    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_8))
    ) {
        Text(
            text = stringResource(R.string.routing),
            style = Typography.labelLarge,
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
            text = stringResource(R.string.end_routing),
            onClick = { onAction(RouteAction.EndRouting) },
        )
    }
}
