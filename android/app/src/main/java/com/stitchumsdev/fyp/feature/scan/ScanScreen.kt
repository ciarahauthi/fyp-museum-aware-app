package com.stitchumsdev.fyp.feature.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.core.model.BeaconId
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
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState) {
                ScanUiState.Error -> {
                    Timber.d("!! Error state")
                    OfflineScreen()
                }
                ScanUiState.Loading -> {
                    Timber.d("!! Loading state")
                    OfflineScreen()
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
fun NoContent(
    onAction: (ScanScreenAction) -> Unit
) {
    Text("No Content!")
    Button(
        onClick = { onAction(ScanScreenAction.GetNearbyObjects) }
    ) {
        Text(
            text = "Refresh",
            style = Typography.titleLarge.copy(
                color = fypColours.mainText
            )
        )
    }

}

@Composable
fun Content(
    list: List<BeaconId>,
    onAction: (ScanScreenAction) -> Unit
) {
    list.forEach { packet ->
        Text("This is a packet uuid ${packet.uuid}, major: ${packet.major}, minor: ${packet.minor}")
    }
    Button(
        onClick = { onAction(ScanScreenAction.GetNearbyObjects) }
    ) {
        Text(
            text = "Refresh",
            style = Typography.titleLarge.copy(
                color = fypColours.mainText
            )
        )
    }
}