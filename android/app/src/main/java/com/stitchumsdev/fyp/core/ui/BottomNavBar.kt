package com.stitchumsdev.fyp.core.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.navigation.Home
import com.stitchumsdev.fyp.core.navigation.Map
import com.stitchumsdev.fyp.core.navigation.Scan
import com.stitchumsdev.fyp.core.navigation.Search
import com.stitchumsdev.fyp.core.ui.theme.fypColours

sealed class NavItem(
    val route: Any,
    val icon: Int
) {
    data object HomeItem: NavItem(Home, R.drawable.ic_home)
    data object MapItem: NavItem(Map, R.drawable.ic_map)
    data object SearchItem: NavItem(Search, R.drawable.ic_search)
    data object ScanItem: NavItem(Scan, R.drawable.ic_scan)
}

@Composable
fun BottomNavigationBar(navHostController: NavHostController) {
    val currentScreen = navHostController.currentBackStackEntry?.destination?.route
    val navBarItems = remember {
        listOf(
            NavItem.HomeItem,
            NavItem.MapItem,
            NavItem.SearchItem,
            NavItem.ScanItem
        )
    }

    NavigationBar(
        containerColor = fypColours.navBar,
        modifier = Modifier
            .height(100.dp)
    ) {
        navBarItems.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.route::class.qualifiedName,
                onClick = { if (currentScreen != item.route) {
                    navHostController.navigate(item.route) }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }
    }
}