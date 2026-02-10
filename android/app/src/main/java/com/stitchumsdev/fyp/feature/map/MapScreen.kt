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
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    uiState: MapUiState,
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
    onClick: (RoomHeatPoint) -> Unit
) {
    val rooms = remember {
        uiState.locations.keys.map { it.toHmPoint() }
    }
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
        set(value) {
            field = value; invalidate()
        }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    init {
        setWillNotDraw(false)
        isClickable = false
        isFocusable = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val iv = imageView ?: return
        if (!iv.isReady) return

        for (p in points) {
            val sx = p.x.coerceIn(0f, 1f) * iv.sWidth
            val sy = p.y.coerceIn(0f, 1f) * iv.sHeight
            val v = iv.sourceToViewCoord(PointF(sx, sy)) ?: continue

            val a = ((p.weight.coerceIn(0f, 3f) / 3f) * 180f).toInt()
            paint.color = Color.argb(255, 255, 0, 0)

            val radius = p.radiusPx * iv.scale
            canvas.drawCircle(v.x, v.y, radius, paint)

        }
    }
}
