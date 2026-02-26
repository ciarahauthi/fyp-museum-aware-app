package com.stitchumsdev.fyp.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours

@Composable
fun FeedbackBox(
    modifier: Modifier = Modifier,
    onRate: (Boolean) -> Unit
) {
    var selection by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var visible by rememberSaveable { mutableStateOf(true) }

    val fade by animateFloatAsState(
        targetValue = if (selection == null) 1f else 0.25f,
        animationSpec = tween(1000)
    )

    val upAlpha = if (selection == false) fade else 1f
    val downAlpha = if (selection == true) fade else 1f

    LaunchedEffect(selection) {
        if (selection != null) {
            delay(1000)
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(tween(1000)) + shrinkVertically(tween(1000)),
    ) {
        Card(
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_small)),
            colors = CardDefaults.cardColors(
                containerColor = fypColours.secondaryBackground,
                contentColor = fypColours.mainText,
            ),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_4)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.experience),
                    style = Typography.titleMedium,
                    color = fypColours.mainText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_24),
                        Alignment.CenterHorizontally
                    ),
                ) {
                    val disableClicks = selection != null

                    IconButton(
                        onClick = {
                            if (!disableClicks) selection = true
                            onRate(true)
                                  },
                        enabled = !disableClicks,
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_default))
                            .alpha(upAlpha)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_thumbs_up),
                            contentDescription = null,
                            tint = fypColours.thumbsUp,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
                        )
                    }

                    IconButton(
                        onClick = {
                            if (!disableClicks) selection = false
                            onRate(false)
                                  },
                        enabled = !disableClicks,
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_default))
                            .alpha(downAlpha)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_thumbs_down),
                            contentDescription = null,
                            tint = fypColours.thumbsDown,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FeedbackBoxPreview() {
    FypTheme {
        FeedbackBox { }
    }
}