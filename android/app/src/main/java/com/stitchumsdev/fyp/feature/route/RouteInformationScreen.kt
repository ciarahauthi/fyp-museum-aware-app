package com.stitchumsdev.fyp.feature.route

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.LocationModel
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.model.RouteModel
import com.stitchumsdev.fyp.core.ui.InformationScreen
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.components.ExhibitRow
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
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Description of route
            Text(
                text = routeInfo.description,
                style = Typography.bodyLarge,
                color = fypColours.mainText,
                textAlign = TextAlign.Start
            )

            // List of Route items
            Text(
                text = stringResource(R.string.items_in_route),
                style = Typography.headlineSmall,
                color = fypColours.mainText,
                textAlign = TextAlign.Start
            )
            HorizontalDivider()

            routeInfo.stops.forEach { stop ->
                ExhibitRow(
                    stop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{ onObjectClick(stop) }
                )
            }

            CommonButton(
                text = stringResource(R.string.start_route),
                onClick = { onStartRoute(routeInfo.nodes) },
            )
        }
    }
}