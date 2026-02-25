package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navHostController: NavHostController,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    showBack: Boolean = true,
) {
    TopAppBar(
        title = title,
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null,
                        tint = fypColours.mainText,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = fypColours.navBar,
            titleContentColor = fypColours.mainText,
            actionIconContentColor = fypColours.mainText,
            navigationIconContentColor = fypColours.mainText
        )
    )
}