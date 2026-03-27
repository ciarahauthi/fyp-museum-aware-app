package com.stitchumsdev.fyp.feature.scan

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ExhibitModel
import com.stitchumsdev.fyp.core.ui.GenericErrorScreen
import com.stitchumsdev.fyp.core.ui.LoadingScreen
import com.stitchumsdev.fyp.core.ui.components.AppInfoBox
import com.stitchumsdev.fyp.core.ui.components.AppModal
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.components.ExhibitRow
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    navHostController: NavHostController,
    uiState: ScanUiState,
    onAction: (ScanScreenAction) -> Unit,
    onObjectClick: (ExhibitModel) -> Unit,
) {
    var showHelpModal by remember { mutableStateOf(false) }

    if (showHelpModal) {
        AppModal(
            visible = true,
            onDismiss = { showHelpModal = false },
            title = stringResource(R.string.scan_help_title)
        ) {
            Text(
                text = stringResource(R.string.scan_help_body),
                style = Typography.bodyMedium,
                color = fypColours.secondaryText
            )
        }
    }

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
                    text = stringResource(R.string.nearby_exhibits),
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
                ScanUiState.Error -> GenericErrorScreen(onRetry = { onAction(ScanScreenAction.GetNearbyObjects) })
                ScanUiState.Loading -> LoadingScreen()
                ScanUiState.BluetoothDisabled -> BluetoothDisabledContent(
                    onRetry = { onAction(ScanScreenAction.GetNearbyObjects) }
                )

                is ScanUiState.Success -> Content(
                    list = uiState.objects,
                    onObjectClick = onObjectClick,
                    onHelpClick = { showHelpModal = true }
                )
            }
        }
    }
}

@Composable
fun BluetoothDisabledContent(
    onRetry: () -> Unit
) {
    val context = LocalContext.current
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
            painter = painterResource(R.drawable.ic_no_scan),
            contentDescription = null,
            tint = fypColours.mainText,
            modifier = Modifier.size(dimensionResource(R.dimen.image_large))
        )
        Text(
            text = stringResource(R.string.bluetooth_disabled),
            style = Typography.titleMedium,
            textAlign = TextAlign.Center,
            color = fypColours.mainText
        )
        CommonButton(
            text = stringResource(R.string.enable_bluetooth),
            onClick = { context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
        )
        CommonButton(
            text = stringResource(R.string.try_again),
            onClick = onRetry
        )
    }
}

@Composable
fun Content(
    list: List<ExhibitModel>,
    onObjectClick: (ExhibitModel) -> Unit,
    onHelpClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_exhibits_nearby),
                    style = Typography.titleMedium,
                    color = fypColours.secondaryText
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
            ) {
                list.forEach { item ->
                    ExhibitRow(
                        obj = item,
                        modifier = Modifier.clickable { onObjectClick(item) }
                    )
                }
            }
        }

        AppInfoBox(
            title = stringResource(R.string.scan_help_box),
            clickable = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onHelpClick() }
                .padding(vertical = dimensionResource(R.dimen.padding_8))
        )
    }
}

@Preview
@Composable
fun ScanScreenPreview() {
    FypTheme {
        ScanScreen(
            navHostController = rememberNavController(),
            uiState = ScanUiState.Success(
                objects = listOf(
                    ExhibitModel(
                        id = 1,
                        title = "Test",
                        description = "true",
                        category = "true",
                        childFriendly = true,
                        isLoud = true,
                        isCrowded = true,
                        isDark = true,
                        likes = 10,
                        dislikes = 10,
                        location = 1,
                        uuid = "1",
                        major = 1,
                        minor = 1,
                        imageUrl = "",
                        canRate = true,
                        createdAt = 1
                    )
                )
            ),
            onAction = {},
            onObjectClick = {}
        )
    }
}