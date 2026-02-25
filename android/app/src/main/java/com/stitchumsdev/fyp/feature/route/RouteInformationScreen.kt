package com.stitchumsdev.fyp.feature.route

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.model.RouteModel
import com.stitchumsdev.fyp.core.ui.InformationScreen
import com.stitchumsdev.fyp.core.ui.components.ListItem
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun RouteInformationScreen(
    navHostController: NavHostController,
    routeInfo: RouteModel,
    onStartRoute: (List<LocationModel>) -> Unit,
    onObjectClick: (ObjectModel) -> Unit
) {
    InformationScreen(
        navHostController = navHostController,
        title = routeInfo.name,
        image = R.drawable.img_rusty // ToDo change
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
        ) {
            // Description of route
            Text(
                text = routeInfo.description,
                style = Typography.bodyLarge,
                color = fypColours.mainText,
            )

            // List of Route items
            Text(
                text = "Items in this route", // ToDo string res
                style = Typography.headlineSmall,
                color = fypColours.mainText
            )
            HorizontalDivider()

            routeInfo.stops.forEach { stop ->
                ListItem(
                    title = stop.title,
                    category = stop.category,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{ onObjectClick(stop) }
                )
            }
        }

        Button(
            onClick = { onStartRoute(routeInfo.nodes) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start route")
        }
    }
}