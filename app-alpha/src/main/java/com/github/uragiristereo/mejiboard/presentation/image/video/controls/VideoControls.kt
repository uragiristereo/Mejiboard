package com.github.uragiristereo.mejiboard.presentation.image.video.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedNavigationBarsPadding
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageState
import com.google.android.exoplayer2.ExoPlayer

@Composable
fun VideoControls(
    player: ExoPlayer,
    state: ImageState,
    videoVolume: Float,
    onVideoVolumeChange: (Float) -> Unit,
    viewModel: ImageViewModel = hiltViewModel(),
) {
    var videoPlayed by rememberSaveable { mutableStateOf(true) }

    AnimatedVisibility(
        visible = state.appBarVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fixedNavigationBarsPadding(),
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp,
                    ),
            ) {
                VolumeSlider(
                    player = player,
                    volumeSliderVisible = state.volumeSliderVisible,
                    onVolumeSliderVisibleChange = { new ->
                        viewModel.state.update { it.copy(volumeSliderVisible = new) }
                    },
                    isVideoHasAudio = state.isVideoHasAudio,
                    videoVolume = videoVolume,
                    onVideoVolumeChange = onVideoVolumeChange,
                )

                PlayerSlider(
                    player = player,
                    videoPlayed = videoPlayed,
                    onVolumeSliderVisibleChange = { new ->
                        viewModel.state.update { it.copy(volumeSliderVisible = new) }
                    },
                )
            }
        }

        PlayPauseButton(
            player = player,
            videoPlayed = videoPlayed,
            onVideoPlayedChange = { videoPlayed = it },
            onVolumeSliderVisibleChange = { new ->
                viewModel.state.update { it.copy(volumeSliderVisible = new) }
            },
        )
    }
}
