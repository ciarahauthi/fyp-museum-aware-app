package com.stitchumsdev.fyp.feature.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.components.TopBar
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun RouteScreen(
    navHostController: NavHostController,
    uiState: RouteUiState,
    onAction: (RouteAction) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navHostController) },
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                    onCreateRoute = {  }, // ToDo
                    onAction = onAction
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
    onCreateRoute: () -> Unit,
    onAction: (RouteAction) -> Unit
) {
    // List of predetermined routes
    Column {  }

    // Create routes
    Column(
        modifier = Modifier
            .background(color = fypColours.secondaryBackground),
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
                color = fypColours.mainText
            )
        )
        Button(
            // ToDo finish ui.
            onClick = { onAction(RouteAction.StartRouting(LocationModel.mock())) }
        ) {
            Text(stringResource(R.string.create_route))
        }

    }
}

@Composable
fun RouteRouting(
    uiState: RouteUiState.Routing,
    onAction: (RouteAction) -> Unit
) {
    val stops = uiState.stops
    val currLoc = uiState.currentLocation

    Text("You are routing")
    Text("Stops left: $stops")
    Text("Current: $currLoc")
    //ToDo remove / update. testing purposes
    Button(
        onClick = { onAction(RouteAction.NextStop) }
    ) {
        Text("Next stop")
    }
}