package com.github.uragiristereo.mejiboard.presentation.image.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.helper.TimeHelper
import com.google.accompanist.insets.navigationBarsPadding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Composable
fun VideoControls(
    player: ExoPlayer,
    controlsVisible: Boolean,
    volumeSliderVisible: Boolean,
    onVolumeSliderVisibleChange: (Boolean) -> Unit,
    isVideoHasAudio: Boolean,
    videoVolume: Float,
    onVideoVolumeChange: (Float) -> Unit,
) {
    var videoPlayed by rememberSaveable { mutableStateOf(true) }

    AnimatedVisibility(
        visible = controlsVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
            ) {
                VolumeSlider(
                    player = player,
                    volumeSliderVisible = volumeSliderVisible,
                    onVolumeSliderVisibleChange = onVolumeSliderVisibleChange,
                    isVideoHasAudio = isVideoHasAudio,
                    videoVolume = videoVolume,
                    onVideoVolumeChange = onVideoVolumeChange,
                )
                PlayerSlider(
                    player = player,
                    videoPlayed = videoPlayed,
                    onVolumeSliderVisibleChange = onVolumeSliderVisibleChange,
                )
            }
        }

        PlayPauseButton(
            player = player,
            videoPlayed = videoPlayed,
            onVideoPlayedChange = { videoPlayed = it },
            onVolumeSliderVisibleChange = onVolumeSliderVisibleChange,
        )
    }
}

@Composable
private fun VolumeSlider(
    player: ExoPlayer,
    volumeSliderVisible: Boolean,
    onVolumeSliderVisibleChange: (Boolean) -> Unit,
    isVideoHasAudio: Boolean,
    videoVolume: Float,
    onVideoVolumeChange: (Float) -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    LaunchedEffect(key1 = videoVolume) {
        player.volume = videoVolume
    }

    Row {
        IconButton(
            onClick = { onVolumeSliderVisibleChange(!volumeSliderVisible) },
        ) {
            Icon(
                painter =
                if (isVideoHasAudio)
                    when {
                        videoVolume >= 0.5f -> painterResource(id = R.drawable.volume_up)
                        videoVolume == 0f -> painterResource(id = R.drawable.volume_mute)
                        else -> painterResource(id = R.drawable.volume_down)
                    }
                else
                    painterResource(id = R.drawable.volume_off),
                contentDescription = null,
                tint = Color.White,
            )
        }
        AnimatedVisibility(
            visible = volumeSliderVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Card(
                elevation = 4.dp,
                modifier = Modifier
                    .width(if (isVideoHasAudio) (screenWidth / 2).dp else Dp.Unspecified)
            ) {
                if (isVideoHasAudio) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "${videoVolume.times(100).roundToInt()}%",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .width(36.dp)
                        )
                        Slider(
                            value = videoVolume,
                            onValueChange = onVideoVolumeChange,
                            onValueChangeFinished = { player.volume = videoVolume },
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .height(48.dp)
                            .padding(all = 16.dp)
                    ) {
                        Text(text = "Video has no audio")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSlider(
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

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            while (true) {
                if (!sliderDragged && !sliderPressed && videoPlayed) {
                    videoProgress = player.currentPosition.toFloat() / player.duration.toFloat()
                    videoPosition = player.currentPosition
                }

                videoDuration = if (player.duration == C.TIME_UNSET) 0 else player.duration

                delay(200)
            }
        }
    }

    LaunchedEffect(
        key1 = videoPosition,
        key2 = sliderDragged,
        key3 = sliderPressed,
    ) {
        videoPositionFmt =
            if (sliderDragged || sliderPressed)
                TimeHelper.formatMillis((videoProgress * videoDuration).roundToLong())
            else
                TimeHelper.formatMillis(videoPosition)
    }

    LaunchedEffect(
        key1 = sliderDragged,
        key2 = sliderPressed,
    ) {
        if (sliderDragged || sliderPressed)
            onVolumeSliderVisibleChange(false)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = videoPositionFmt,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(48.dp)
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
            modifier = Modifier
                .weight(1f, true)
        )
        Text(
            text = TimeHelper.formatMillis(videoDuration),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(48.dp)
        )
    }
}

@Composable
private fun PlayPauseButton(
    player: ExoPlayer,
    videoPlayed: Boolean,
    onVideoPlayedChange: (Boolean) -> Unit,
    onVolumeSliderVisibleChange: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = {
                onVideoPlayedChange(!videoPlayed)
                onVolumeSliderVisibleChange(false)
                player.playWhenReady = !videoPlayed
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(0.2f))
        ) {
            Icon(
                painter = if (videoPlayed) painterResource(R.drawable.pause) else rememberVectorPainter(Icons.Outlined.PlayArrow),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
            )
        }
    }
}