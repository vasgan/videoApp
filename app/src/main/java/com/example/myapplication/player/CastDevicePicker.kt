package com.example.myapplication.player

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.media3.cast.MediaRouteButtonFactory
import androidx.mediarouter.app.MediaRouteButton
import androidx.compose.ui.viewinterop.AndroidView

/** Connects the existing player toolbar button to the system Cast device picker. */
@Composable
fun rememberCastDevicePicker(): () -> Unit {
    val context = LocalContext.current
    val routeButton = remember(context) {
        MediaRouteButton(context)
    }

    DisposableEffect(routeButton) {
        MediaRouteButtonFactory.setUpMediaRouteButton(context, routeButton)
        onDispose { }
    }

    // The route button must be attached to the view hierarchy for MediaRouter
    // to display its picker. The visible toolbar icon remains the app's own button.
    AndroidView(
        factory = { routeButton },
        modifier = Modifier
            .size(1.dp)
            .alpha(0f)
    )

    return remember(routeButton) { { routeButton.performClick() } }
}
