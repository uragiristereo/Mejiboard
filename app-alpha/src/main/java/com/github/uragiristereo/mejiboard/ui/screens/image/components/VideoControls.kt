package com.github.uragiristereo.mejiboard.ui.screens.image.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.util.TimeHelper
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
    setVolumeSliderVisible: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sliderInteraction = remember { MutableInteractionSource() }
    val sliderDragged by sliderInteraction.collectIsDraggedAsState()
    val sliderPressed by sliderInteraction.collectIsPressedAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp

    var videoProgress by remember { mutableStateOf(0f) }
    var videoPlayed by remember { mutableStateOf(true) }
    var videoPosition by remember { mutableStateOf(0L) }
    var videoPositionFmt by remember { mutableStateOf("") }
    var videoDuration by remember { mutableStateOf(0L) }
    var videoDurationFmt by remember { mutableStateOf("") }
    var videoVolume by remember { mutableStateOf(0.5f) }
    
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

    LaunchedEffect(key1 = videoPosition, key2 = sliderDragged, key3 = sliderPressed) {
        videoPositionFmt =
            if (sliderDragged || sliderPressed)
                TimeHelper.formatMillis((videoProgress * videoDuration).roundToLong())
            else
                TimeHelper.formatMillis(videoPosition)

        videoDurationFmt = TimeHelper.formatMillis(videoDuration)
    }

    LaunchedEffect(key1 = sliderDragged, key2 = sliderPressed) {
        if (sliderDragged || sliderPressed)
            setVolumeSliderVisible(false)
    }

    LaunchedEffect(key1 = videoVolume) {
        player.volume = videoVolume
    }

    AnimatedVisibility( // video controls
        visible = controlsVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box( // video controls (with navbar padding)
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    ),
            ) {
                AnimatedVisibility( // volume slider
                    visible = volumeSliderVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Card(
                        elevation = 4.dp,
                        modifier = Modifier
                            .width((screenWidth / 2).dp)
                    ) {
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
                                onValueChange = { videoVolume = it },
                                onValueChangeFinished = { player.volume = videoVolume },
                                modifier = Modifier
                                    .weight(weight = 1f, fill = true)
                            )
                        }
                    }
                }
                Row( // main video controls
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = videoPositionFmt,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(44.dp)
                    )
                    Slider(
                        value = videoProgress,
                        onValueChange = {
                            videoProgress = it
                        },
                        interactionSource = sliderInteraction,
                        onValueChangeFinished = {
                            val newPosition = (player.duration * videoProgress).roundToLong()

                            player.seekTo(newPosition)
                            videoPositionFmt = TimeHelper.formatMillis(newPosition)
                            videoDurationFmt = TimeHelper.formatMillis(newPosition)
                        },
                        modifier = Modifier
                            .weight(1f, true)
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = videoDurationFmt,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(44.dp)
                    )
                    IconButton(
                        onClick = { setVolumeSliderVisible(!volumeSliderVisible) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.volume_up),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            }
        }

        Box( // play/pause control
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                onClick = {
                    setVolumeSliderVisible(false)
                    videoPlayed = !videoPlayed
                    player.playWhenReady = videoPlayed
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
}