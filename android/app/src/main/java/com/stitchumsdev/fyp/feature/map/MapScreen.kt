package com.stitchumsdev.fyp.feature.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.RoomHeatPoint
import com.stitchumsdev.fyp.core.ui.components.AppModal
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours
import com.stitchumsdev.fyp.feature.route.RouteUiState
import androidx.compose.ui.graphics.Color as composeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    routeUiState: RouteUiState,
    navHostController: NavHostController
) {
    var selectedPoint by remember { mutableStateOf<RoomHeatPoint?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (uiState) {
                MapUiState.Error -> {}
                MapUiState.Loading -> {}
                is MapUiState.Success -> MapSuccess(
                    uiState = uiState,
                    routeUiState = routeUiState,
                    onClick = { point ->
                        selectedPoint = point
                    }
                )
            }

            // Room detail modal
            if (selectedPoint != null) {
                AppModal(
                    visible = selectedPoint != null,
                    onDismiss = { selectedPoint = null },
                    title = selectedPoint?.name ?: ""
                ) {
                    MapModalContent(
                        uiState = uiState,
                        selectedPoint = selectedPoint)
                }
            }
        }
    }
}

// Success state when map loads properly
// Displays current user location
// While routing, also shows current location, where the current targets are and where next stop is
@Composable
fun MapSuccess(
    uiState: MapUiState.Success,
    routeUiState: RouteUiState,
    onClick: (RoomHeatPoint) -> Unit
) {
    val userLocId = uiState.currentLocation?.id
    val rooms = remember { uiState.locations.keys.map { it.toHmPoint() } }

    // For Routing state
    val routing = routeUiState as? RouteUiState.Routing
    val currentId = routing?.currentTarget?.id
    val nextId = routing?.nextTarget?.id

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),

            factory = { context ->
                // Container to place map view + overlay view on top of each other
                val container = FrameLayout(context)

                // Zoom & pan image view
                val mapView = SubsamplingScaleImageView(context).apply {
                    setImage(ImageSource.resource(R.drawable.img_ground_floor))
                }


                // Align heatmap points to map image
                val overlay = HeatOverlayView(context).apply {
                    imageView = mapView
                    points = rooms
                    currentTargetId = currentId
                    nextTargetId = nextId
                    userLocationId = userLocId
                }

                // Detect point clicks
                val tapDetector = android.view.GestureDetector(
                    context,
                    object : android.view.GestureDetector.SimpleOnGestureListener() {

                        // OnClick detection
                        override fun onDown(e: android.view.MotionEvent): Boolean {
                            return true
                        }

                        override fun onSingleTapUp(e: android.view.MotionEvent): Boolean {
                            if (!mapView.isReady) return false

                            val hit = findNearestRoom(mapView, rooms, e.x, e.y)
                            if (hit != null) {
                                onClick(hit)
                                return true
                            }
                            return false
                        }
                    }
                )

                mapView.setOnTouchListener { _, event ->
                    if (event.pointerCount == 1) {
                        tapDetector.onTouchEvent(event)
                    }
                    false
                }

                // keep overlay w/ points aligned to image
                mapView.viewTreeObserver.addOnPreDrawListener {
                    overlay.invalidate()
                    true
                }

                container.addView(mapView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
                container.addView(overlay, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

                container
            },
            update = { container ->
                val overlay = container.getChildAt(1) as HeatOverlayView
                overlay.points = rooms
                overlay.currentTargetId = currentId
                overlay.nextTargetId = nextId
                overlay.userLocationId = userLocId
            }
        )

        // Map Legend
        if (routing != null) {
            DotLegend(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensionResource(R.dimen.padding_8))
                    .wrapContentSize()
            )
        }
    }
}


private fun findNearestRoom(
    imageView: SubsamplingScaleImageView,
    roomPoints: List<RoomHeatPoint>,
    tapX: Float,
    tapY: Float
): RoomHeatPoint? {

    var closestPoint: RoomHeatPoint? = null
    var closestDistSquared = Float.MAX_VALUE

    for (point in roomPoints) {
        // Convert coords to image pixels
        val sourceX = point.x * imageView.sWidth
        val sourceY = point.y * imageView.sHeight

        // Convert image pixels to screen/view coords taking pan and zoom into account
        val viewCoord = imageView.sourceToViewCoord(PointF(sourceX, sourceY)) ?: continue

        // Hit radius scales with zoom
        val hitRadius = maxOf(28f, point.radiusPx * imageView.scale)

        // Distance from tap to point
        val dx = tapX - viewCoord.x
        val dy = tapY - viewCoord.y
        val distSquared = dx * dx + dy * dy

        // Keep the closest point
        if (distSquared <= hitRadius * hitRadius && distSquared < closestDistSquared) {
            closestPoint = point
            closestDistSquared = distSquared
        }
    }
    return closestPoint
}

class HeatOverlayView(context: Context) : View(context) {
    var imageView: SubsamplingScaleImageView? = null
    var points: List<RoomHeatPoint> = emptyList()
        set(value) { field = value; invalidate() }

    var currentTargetId: Int? = null
        set(value) { field = value; invalidate() }

    var nextTargetId: Int? = null
        set(value) { field = value; invalidate() }

    var userLocationId: Int? = null
        set(value) { field = value; invalidate() }

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private var pulse: Float = 0f
    private val pulseAnimator = android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 900L
        repeatMode = android.animation.ValueAnimator.REVERSE
        repeatCount = android.animation.ValueAnimator.INFINITE
        addUpdateListener {
            pulse = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        setWillNotDraw(false)
        isClickable = false
        isFocusable = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!pulseAnimator.isStarted) pulseAnimator.start()
    }

    override fun onDetachedFromWindow() {
        pulseAnimator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val iv = imageView ?: return
        if (!iv.isReady) return

        for (p in points) {
            val sx = p.x.coerceIn(0f, 1f) * iv.sWidth
            val sy = p.y.coerceIn(0f, 1f) * iv.sHeight
            val v = iv.sourceToViewCoord(PointF(sx, sy)) ?: continue
            val isUserLocation = (p.id == userLocationId)

            val radius = p.radiusPx * iv.scale

            val isCurrent = (p.id == currentTargetId)
            val isNext = (p.id == nextTargetId)

            // Base dot
            basePaint.color = Color.argb(120, 255, 0, 0)
            canvas.drawCircle(v.x, v.y, radius, basePaint)

            // Highlight styles
            when {
                isUserLocation -> drawGlow(canvas, v.x, v.y, radius, Color.BLUE)
                isCurrent -> drawGlow(canvas, v.x, v.y, radius, Color.GREEN)
                isNext -> drawGlow(canvas, v.x, v.y, radius, Color.YELLOW)
            }
        }
    }

    // Draw glow around map points
    private fun drawGlow(canvas: Canvas, x: Float, y: Float, r: Float, color: Int) {
        // Point glow
        val glowStrength = (0.25f + 0.75f * pulse) // pulse affects glow
        val glowAlpha1 = (70f * glowStrength).toInt()
        val glowAlpha2 = (40f * glowStrength).toInt()

        basePaint.color = Color.argb(glowAlpha2, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawCircle(x, y, r * 2.2f, basePaint)

        basePaint.color = Color.argb(glowAlpha1, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawCircle(x, y, r * 1.6f, basePaint)

        // Center dot
        basePaint.color = Color.argb(230, Color.red(color), Color.green(color), Color.blue(color))
        canvas.drawCircle(x, y, r * 1.05f, basePaint)

        // Pulsing ring
        ringPaint.color = Color.argb(200, Color.red(color), Color.green(color), Color.blue(color))
        ringPaint.strokeWidth = maxOf(3f, r * 0.15f)
        val ringRadius = r * (1.2f + 0.8f * pulse)
        canvas.drawCircle(x, y, ringRadius, ringPaint)
    }
}

@Composable
fun DotLegend(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = fypColours.secondaryBackground,
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium))
            )
            .padding(dimensionResource(R.dimen.padding_8)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
    ) {
        Text(
            text = "Map key",
            style = Typography.titleSmall,
            color = fypColours.mainText
        )
        HorizontalDivider()

        LegendRow(color = composeColor.Blue, label = "You are here")
        LegendRow(color = composeColor.Green, label = "Current stop")
        LegendRow(color = composeColor.Yellow, label = "Next stop")
    }
}

@Composable
private fun LegendRow(
    color: composeColor,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.size_small))
                .clip(CircleShape)
                .background(color)
        )

        Text(
            text = label,
            style = Typography.bodyMedium,
            color = fypColours.secondaryText
        )
    }
}

@Composable
private fun MapModalContent(
    uiState: MapUiState,
    selectedPoint: RoomHeatPoint?
) {
    val success = uiState as? MapUiState.Success ?: return
    val point = selectedPoint ?: return

    val items = success.locations[point.toLocationModel()].orEmpty()

    Text(
        text = stringResource(R.string.objects_here) + " (${items.size})",
        style = Typography.titleSmall,
        color = fypColours.mainText
    )

    if (items.isEmpty()) {
        Text(
            text = stringResource(R.string.no_objects_in_room),
            style = Typography.bodyMedium,
            color = fypColours.secondaryText
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_8))
        ) {
            items(items) { obj ->
                Text(
                    text = "• ${obj.title}",
                    style = Typography.bodyMedium,
                    color = fypColours.secondaryText
                )
            }
        }
    }
}