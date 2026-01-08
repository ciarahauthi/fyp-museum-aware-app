package com.stitchumsdev.fyp.feature.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.core.navigation.Exhibit
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.components.TopBar
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun TestScreen(
    navHostController: NavHostController,
    onAction: (TestScreenAction) -> Unit
) {
    LaunchedEffect(Unit) { onAction(TestScreenAction.FetchUsers) }
    Scaffold(
        topBar = { TopBar(navHostController) },
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(fypColours.mainBackground)
        ) {
            Text(
                text = "Hello HomeScreen!",
                modifier = Modifier.padding(innerPadding),
                style = Typography.titleLarge.copy(
                    color = fypColours.mainText
                )
            )
            Button(onClick = { navHostController.navigate(Exhibit) }) {
                Text(
                    text = "Click",
                    style = Typography.titleLarge.copy(
                        color = fypColours.mainText
                    )
                )
            }
        }
    }
}