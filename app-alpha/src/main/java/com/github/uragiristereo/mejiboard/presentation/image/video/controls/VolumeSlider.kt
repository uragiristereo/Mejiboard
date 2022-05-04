package com.github.uragiristereo.mejiboard.presentation.image.video.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.google.android.exoplayer2.ExoPlayer
import kotlin.math.roundToInt

@Composable
fun VolumeSlider(
    player: ExoPlayer,
    volumeSliderVisible: Boolean,
    onVolumeSliderVisibleChange: (Boolean) -> Unit,
    isVideoHasAudio: Boolean,
    videoVolume: Float,
    onVideoVolumeChange: (Float) -> Unit,
) {
    var videoVolumeState by remember { mutableStateOf(videoVolume * 10) }
    var lastVideoVolume by remember { mutableStateOf(videoVolumeState) }
    var videoVolumeText by remember { mutableStateOf("${videoVolume.times(100).roundToInt()}%") }

    LaunchedEffect(key1 = videoVolume) {
        player.volume = videoVolume
    }

    Row {
        IconButton(
            onClick = { onVolumeSliderVisibleChange(!volumeSliderVisible) },
            content = {
                Icon(
                    painter =
                    if (isVideoHasAudio)
                        when {
                            videoVolumeState >= 5f -> painterResource(id = R.drawable.volume_up)
                            videoVolumeState == 0f -> painterResource(id = R.drawable.volume_mute)
                            else -> painterResource(id = R.drawable.volume_down)
                        }
                    else
                        painterResource(id = R.drawable.volume_off),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        )
        AnimatedVisibility(
            visible = volumeSliderVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Card(
                elevation = 4.dp,
                modifier = Modifier.width(if (isVideoHasAudio) 200.dp else Dp.Unspecified),
            ) {
                if (isVideoHasAudio) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = videoVolumeText,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .width(36.dp)
                        )

                        Slider(
                            value = videoVolumeState,
                            onValueChange = {
                                videoVolumeState = it
                                val volume = it.roundToInt().toFloat()

                                if (lastVideoVolume != volume) {
                                    lastVideoVolume = volume
                                    videoVolumeText = "${volume.times(10).roundToInt()}%"
                                    player.volume = volume / 10
                                }
                            },
                            onValueChangeFinished = {
                                onVideoVolumeChange(videoVolumeState.roundToInt().toFloat() / 10)
                            },
                            valueRange = 0f..10f,
                            steps = 10 - 1,
                            colors = SliderDefaults.colors(
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent,
                            ),
                            modifier = Modifier.weight(weight = 1f, fill = true),
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .height(48.dp)
                            .padding(all = 16.dp),
                    ) {
                        Text(text = "Video has no audio")
                    }
                }
            }
        }
    }
}