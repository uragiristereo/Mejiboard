package com.github.uragiristereo.mejiboard.ui.screens.image.components.video

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerView

@ExperimentalMaterialApi
@Composable
fun VideoPlayer(
    playerView: PlayerView,
    onPress: () -> Unit,
    onLongPress: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onPress = { onPress() }
                )
            }
    ) {
        AndroidView(
            factory = { playerView }
        )
    }
}