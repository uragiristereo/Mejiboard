package com.github.uragiristereo.mejiboard.presentation.image.video.controls

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.common.helper.TimeHelper
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@Composable
fun PlayerSlider(
    player: ExoPlayer,
    videoPlayed: Boolean,
    onVolumeSliderVisibleChange: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sliderInteraction = remember { MutableInteractionSource() }
    val sliderDragged by sliderInteraction.collectIsDraggedAsState()
    val sliderPressed by sliderInteraction.collectIsPressedAsState()

    var videoProgress by remember { mutableStateOf(0f) }
    var videoPosition by remember { mutableStateOf(0L) }
    var videoPositionFmt by remember { mutableStateOf("") }
    var videoDuration by remember { mutableStateOf(0L) }

    DisposableEffect(key1 = Unit) {
        val job = scope.launch {
            while (true) {
                if (!sliderDragged && !sliderPressed && videoPlayed) {
                    videoProgress = player.currentPosition.toFloat() / player.duration.toFloat()
                    videoPosition = player.currentPosition
                }

                videoDuration = when (player.duration) {
                    C.TIME_UNSET -> 0
                    else -> player.duration
                }

                delay(timeMillis = 200L)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    LaunchedEffect(
        key1 = videoPosition,
        key2 = sliderDragged,
        key3 = sliderPressed,
    ) {
        videoPositionFmt = when {
            sliderDragged || sliderPressed -> TimeHelper.formatMillis((videoProgress * videoDuration).roundToLong())
            else -> TimeHelper.formatMillis(videoPosition)
        }
    }

    LaunchedEffect(
        key1 = sliderDragged,
        key2 = sliderPressed,
    ) {
        if (sliderDragged || sliderPressed)
            onVolumeSliderVisibleChange(false)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = videoPositionFmt,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(48.dp),
        )

        Slider(
            value = videoProgress,
            onValueChange = {
                videoPositionFmt = TimeHelper.formatMillis((it * videoDuration).roundToLong())
                videoProgress = it
            },
            interactionSource = sliderInteraction,
            onValueChangeFinished = {
                val newPosition = (player.duration * videoProgress).roundToLong()

                player.seekTo(newPosition)
                videoPosition = newPosition
            },
            modifier = Modifier.weight(1f, true),
        )

        Text(
            text = TimeHelper.formatMillis(videoDuration),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(48.dp),
        )
    }
}