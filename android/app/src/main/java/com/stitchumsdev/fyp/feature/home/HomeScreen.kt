package com.stitchumsdev.fyp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ExhibitModel
import com.stitchumsdev.fyp.core.model.HomeItem
import com.stitchumsdev.fyp.core.ui.LoadingScreen
import com.stitchumsdev.fyp.core.ui.OfflineErrorScreen
import com.stitchumsdev.fyp.core.ui.components.AppModal
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.components.CommonButton
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours
import timber.log.Timber

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    uiState: HomeUiState,
    onExhibitClick: (ExhibitModel) -> Unit,
    onRetry: () -> Unit
) {
    var modalVisible by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<HomeItem?>(null) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Error -> OfflineErrorScreen(onRetry = onRetry)
            HomeUiState.Loading -> LoadingScreen()
            is HomeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fypColours.mainBackground)
                        .padding(innerPadding)
                        .padding(horizontal = dimensionResource(R.dimen.padding_8))
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_16))
                ) {
                    HomeSuccess(
                        uiState = uiState,
                        onCardClick = { card ->
                            modalVisible = true
                            selectedCard = card
                        },
                        onExhibitClick = onExhibitClick
                    )
                }
            }
        }

        selectedCard?.let {
            Timber.d(("!! IMG ${it.imageUrl}"))
            AppModal(
                visible = modalVisible,
                onDismiss = { modalVisible = false },
                title = it.title
            ) {
                val imageUrl = it.imageUrl?.takeIf { it.isNotBlank() }
                val imageModifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.image_x_large))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_medium)))
                if (imageUrl != null) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
                            modifier = Modifier.size(32.dp),
                            tint = fypColours.secondaryText
                        )
                    }
                }
                Text(it.description)
            }
        }
    }
}

@Composable
fun HomeSuccess(
    uiState: HomeUiState.Success,
    onCardClick: (HomeItem) -> Unit,
    onExhibitClick: (ExhibitModel) -> Unit
) {
    val topItems = uiState.topSection
    val midItems = uiState.midSection
    val bottomItems = uiState.bottomSection
    var categorySelected by rememberSaveable { mutableStateOf(HomeFeedCategory.Popular) }
    val visibleExhibits by remember(categorySelected, uiState.popular, uiState.new) {
        derivedStateOf {
            when (categorySelected) {
                HomeFeedCategory.Popular -> uiState.popular
                HomeFeedCategory.New -> uiState.new
            }
        }
    }

    Text(
        text = stringResource(R.string.home_heading),
        style = Typography.titleSmall.copy(
            color = fypColours.mainText
        )
    )
    HorizontalDivider()

    // Exhibits Section
    // ToDo make curated
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
    ) {
        Text(
            text = stringResource(R.string.interested_exhibits),
            style = Typography.titleMedium.copy(
                color = fypColours.mainText
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
        ) {
            CommonButton(
                text = "Popular",
                selected = categorySelected == HomeFeedCategory.Popular,
                onClick = { categorySelected = HomeFeedCategory.Popular }
            )

            CommonButton(
                text = "New",
                selected = categorySelected == HomeFeedCategory.New,
                onClick = { categorySelected = HomeFeedCategory.New }
            )
        }
        HomeCardHorizontalScroll(
            exhibitItems = visibleExhibits,
            onExhibitClick = onExhibitClick
        )
    }

    HorizontalDivider()

    // ToDo a categories / collections section

    // TopSection
    if (topItems.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
        ) {
            Text(
                text = stringResource(R.string.news),
                style = Typography.titleMedium.copy(
                    color = fypColours.mainText
                )
            )

            if (topItems.size == 1) {
                HomeCard(
                    homeItem = topItems.first(),
                    modifier = Modifier.fillMaxWidth(),
                    onCardClick = onCardClick
                )
            } else {
                HomeCardHorizontalScroll(homeItems = topItems, onCardClick = onCardClick)
            }
            HorizontalDivider()
        }
    }

    // Midsection
    if (midItems.isNotEmpty()) {
        if (midItems.size == 1) {
            HomeCard(
                homeItem = midItems.first(),
                modifier = Modifier.fillMaxWidth(),
                onCardClick = onCardClick
            )
        } else {
            HomeCardHorizontalScroll(homeItems = midItems, onCardClick = onCardClick)
        }
        HorizontalDivider()
    }

    // Bottom Section
    if (bottomItems.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
        ) {
            Text(
                text = stringResource(R.string.events_and_workshops),
                style = Typography.titleMedium.copy(
                    color = fypColours.mainText
                )
            )
            if (bottomItems.size == 1) {
                HomeCard(
                    homeItem = bottomItems.first(),
                    modifier = Modifier.fillMaxWidth(),
                    onCardClick = onCardClick
                )
            } else {
                HomeCardHorizontalScroll(
                    homeItems = bottomItems,
                    onCardClick = onCardClick
                )
            }
        }
    }
}

@Composable
fun HomeCardHorizontalScroll(
    homeItems: List<HomeItem>? = null,
    exhibitItems: List<ExhibitModel>? = null,
    onCardClick: (HomeItem) -> Unit = {},
    onExhibitClick: (ExhibitModel) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_8))
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
    ) {
        homeItems?.forEach {
            HomeCard(
                homeItem = it,
                modifier = Modifier
                    .width(220.dp)
                    .height(170.dp),
                onCardClick = onCardClick
            )
        }

        exhibitItems?.forEach {
            ExhibitHomeCard(
                exhibit = it,
                modifier = Modifier
                    .width(220.dp)
                    .height(190.dp),
                onExhibitClick = onExhibitClick
            )
        }
    }
}

@Composable
fun HomeCard(
    homeItem: HomeItem,
    modifier: Modifier = Modifier,
    onCardClick: (HomeItem) -> Unit
) {
    val hasImage = !homeItem.imageUrl.isNullOrBlank()

    Card(
        modifier = modifier
            .clickable { onCardClick(homeItem) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium)),
        colors = CardDefaults.cardColors(containerColor = fypColours.secondaryBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_8)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4))
        ) {
            // Title always on top
            Text(
                text = homeItem.title,
                style = Typography.titleSmall,
                color = fypColours.mainText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (hasImage) {
                AsyncImage(
                    model = homeItem.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 200.dp)
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_medium)))
                )
            } else {
                Text(
                    text = homeItem.description,
                    style = Typography.bodyMedium,
                    color = fypColours.secondaryText,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ExhibitHomeCard(
    exhibit: ExhibitModel,
    modifier: Modifier = Modifier,
    onExhibitClick: (ExhibitModel) -> Unit
) {

    Card(
        modifier = modifier
            .clickable { onExhibitClick(exhibit) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium)),
        colors = CardDefaults.cardColors(containerColor = fypColours.secondaryBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_8)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4))
        ) {
            val imageUrl = exhibit.imageUrl?.takeIf { it.isNotBlank() }
            val imageModifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_medium)))
            if (imageUrl != null) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
                        modifier = Modifier.size(32.dp),
                        tint = fypColours.secondaryText
                    )
                }
            }

            // Title
            Text(
                text = exhibit.title,
                style = Typography.titleSmall.copy(
                    color = fypColours.mainText,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Description
            Text(
                text = exhibit.description,
                style = Typography.bodyMedium,
                color = fypColours.secondaryText,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

enum class HomeFeedCategory { Popular, New }

@Preview
@Composable
fun HomeScreenPreview() {
    FypTheme {
        HomeScreen(
            rememberNavController(),
            uiState = HomeUiState.Success(),
            onExhibitClick = {},
            onRetry = {}
        )
    }
}