package com.github.uragiristereo.mejiboard.presentation.image.video

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageState
import com.github.uragiristereo.mejiboard.presentation.image.video.controls.VideoControls
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@ExperimentalMaterialApi
@Composable
fun VideoPost(
    state: ImageState,
    okHttpClient: OkHttpClient,
    sheetState: ModalBottomSheetState,
    videoVolume: Float,
    onVideoVolumeChange: (Float) -> Unit,
    viewModel: ImageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val post = state.selectedPost!!

    val videoUrl = remember { post.originalImage.url }
    val playerView = remember { StyledPlayerView(context) }
    val player = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(OkHttpDataSource.Factory(okHttpClient)))
            .build()
    }

    DisposableEffect(key1 = Unit) {
        val cacheFactory = CacheDataSource.Factory().apply {
            setCache(viewModel.exoPlayerCache)
            setUpstreamDataSourceFactory(
                DefaultDataSource.Factory(
                    context,
                    DefaultHttpDataSource.Factory().setUserAgent(Constants.USER_AGENT)
                )
            )
        }
        val mediaItem = MediaItem.fromUri(videoUrl)

        player.apply {
            setMediaItem(mediaItem)
            setMediaSource(
                ProgressiveMediaSource.Factory(cacheFactory)
                    .createMediaSource(mediaItem)
            )
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            volume = videoVolume
            addListener(object : Player.Listener {
                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)

                    viewModel.state.update { it.copy(isVideoHasAudio = false) }

                    tracks.groups.forEach { trackGroupInfo ->
                        for (i in 0 until trackGroupInfo.mediaTrackGroup.length) {
                            val trackMimeType = trackGroupInfo.mediaTrackGroup.getFormat(i).sampleMimeType
                            if (trackMimeType?.contains("audio") == true)
                                viewModel.state.update { it.copy(isVideoHasAudio = true) }
                        }
                    }
                }
            })
        }

        playerView.apply {
            this.player = player
            useController = false
            controllerAutoShow = false

            videoSurfaceView?.isHapticFeedbackEnabled = false

            videoSurfaceView?.setOnLongClickListener {
                scope.launch {
                    viewModel.state.update {
                        it.copy(
                            appBarVisible = true,
                            volumeSliderVisible = false,
                        )
                    }

                    sheetState.animateTo(ModalBottomSheetValue.Expanded)
                }
                return@setOnLongClickListener true
            }

            videoSurfaceView?.setOnClickListener {
                if (!state.volumeSliderVisible)
                    viewModel.state.update { it.copy(appBarVisible = !it.appBarVisible) }
                else
                    viewModel.state.update { it.copy(volumeSliderVisible = false) }
            }
        }

        player.prepare()

        onDispose {
            player.release()
        }
    }

    LaunchedEffect(key1 = videoVolume) {
        player.volume = videoVolume

        onVideoVolumeChange(videoVolume)
    }

    VideoPlayer(
        playerView = playerView,
        onPress = {
            if (!state.volumeSliderVisible)
                viewModel.state.update { it.copy(appBarVisible = !it.appBarVisible) }
            else
                viewModel.state.update { it.copy(volumeSliderVisible = false) }
        },
        onLongPress = {
            scope.launch {
                viewModel.state.update {
                    it.copy(
                        appBarVisible = true,
                        volumeSliderVisible = false,
                    )
                }

                sheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
    )

    VideoControls(
        state = state,
        player = player,
        videoVolume = videoVolume,
        onVideoVolumeChange = onVideoVolumeChange,
    )
}