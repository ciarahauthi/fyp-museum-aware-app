package com.stitchumsdev.fyp.feature.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.RoomHeatPoint
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.feature.route.RouteUiState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    uiState: MapUiState,
    routeUiState: RouteUiState,
    navHostController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
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
                        val items = uiState.locations[point.toLocationModel()]
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "${point.name}: x=${"%.3f".format(point.x)}, y=${"%.3f".format(point.y)}" +
                                        "\n Items here: $items"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MapSuccess(
    uiState: MapUiState.Success,
    routeUiState: RouteUiState,
    onClick: (RoomHeatPoint) -> Unit
) {
    val rooms = remember {
        uiState.locations.keys.map { it.toHmPoint() }
    }
    val routing = routeUiState as? RouteUiState.Routing
    val currentId = routing?.currentTarget?.id
    val nextId = routing?.nextTarget?.id

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { context ->
            val container = FrameLayout(context)

            val mapView = SubsamplingScaleImageView(context).apply {
                setImage(ImageSource.resource(R.drawable.img_ground_floor))
            }

            val overlay = HeatOverlayView(context).apply {
                imageView = mapView
                points = rooms
                currentTargetId = currentId
                nextTargetId = nextId
            }

            val tapDetector = android.view.GestureDetector(
                context,
                object : android.view.GestureDetector.SimpleOnGestureListener() {

                    // OnClick detection
                    override fun onDown(e: android.view.MotionEvent): Boolean {
                        return true
                    }

                    override fun onSingleTapUp(e: android.view.MotionEvent): Boolean {
                        if (!mapView.isReady) return false

                        val hit = findHitRoom(mapView, rooms, e.x, e.y)
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
        }
    )
}


private fun findHitRoom(
    iv: SubsamplingScaleImageView,
    points: List<RoomHeatPoint>,
    tapX: Float,
    tapY: Float
): RoomHeatPoint? {

    var best: RoomHeatPoint? = null
    var bestDist2 = Float.MAX_VALUE

    for (p in points) {
        val sx = p.x * iv.sWidth
        val sy = p.y * iv.sHeight
        val v = iv.sourceToViewCoord(PointF(sx, sy)) ?: continue

        val r = maxOf(28f, p.radiusPx * iv.scale) // min tap size
        val dx = tapX - v.x
        val dy = tapY - v.y
        val dist2 = dx * dx + dy * dy

        if (dist2 <= r * r && dist2 < bestDist2) {
            best = p
            bestDist2 = dist2
        }
    }
    return best
}

class HeatOverlayView(context: Context) : View(context) {
    var imageView: SubsamplingScaleImageView? = null
    var points: List<RoomHeatPoint> = emptyList()
        set(value) { field = value; invalidate() }

    var currentTargetId: Int? = null
        set(value) { field = value; invalidate() }

    var nextTargetId: Int? = null
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

            val radius = p.radiusPx * iv.scale

            val isCurrent = (p.id == currentTargetId)
            val isNext = (p.id == nextTargetId)

            // Base dot
            basePaint.color = Color.argb(120, 255, 0, 0)
            canvas.drawCircle(v.x, v.y, radius, basePaint)

            // Highlight styles
            when {
                isCurrent -> drawGlow(canvas, v.x, v.y, radius, Color.GREEN)
                isNext -> drawGlow(canvas, v.x, v.y, radius, Color.YELLOW)
            }
        }
    }

    // For routing state, draw glow around map points
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
