package com.github.uragiristereo.mejiboard.ui.screens.image.components.video

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.github.uragiristereo.mejiboard.model.network.Post
import com.github.uragiristereo.mejiboard.ui.screens.image.ImageViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.TracksInfo
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun VideoPost(
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel,
    post: Post,
    appBarVisible: MutableState<Boolean>,
    sheetState: ModalBottomSheetState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imageType = remember { File(post.image).extension }
    val videoUrl = remember { "https://video-cdn3.gelbooru.com/images/${post.directory}/${post.hash}.$imageType" }
    val volumeSliderVisible = remember { mutableStateOf(false) }
    val isVideoHasAudio = remember { mutableStateOf(false) }
    val playerView = remember { PlayerView(context) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    OkHttpDataSource.Factory(mainViewModel.okHttpClient)
                )
            )
            .build()
    }

    DisposableEffect(key1 = Unit) {
        val cacheFactory = CacheDataSource.Factory().apply {
            setCache(imageViewModel.exoPlayerCache)
            setUpstreamDataSourceFactory(
                DefaultDataSource.Factory(
                    context,
                    DefaultHttpDataSource.Factory()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0")
                )
            )
        }
        val mediaItem = MediaItem.fromUri(videoUrl)

        exoPlayer.apply {
            setMediaItem(mediaItem)
            setMediaSource(
                ProgressiveMediaSource.Factory(cacheFactory)
                    .createMediaSource(mediaItem)
            )
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            volume = 0.5f
            addListener(object : Player.Listener {
                override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
                    super.onTracksInfoChanged(tracksInfo)

                    isVideoHasAudio.value = false

                    tracksInfo.trackGroupInfos.forEach {
                        for (i in 0 until it.trackGroup.length) {
                            val trackMimeType = it.trackGroup.getFormat(i).sampleMimeType
                            if (trackMimeType?.contains("audio") == true)
                                isVideoHasAudio.value = true
                        }
                    }
                }
            })
        }

        playerView.apply {
            player = exoPlayer
            useController = false
            controllerAutoShow = false

            videoSurfaceView?.isHapticFeedbackEnabled = false
            videoSurfaceView?.setOnLongClickListener {
                scope.launch {
                    appBarVisible.value
                    volumeSliderVisible.value = false
                    sheetState.animateTo(ModalBottomSheetValue.Expanded)
                }
                return@setOnLongClickListener true
            }
            videoSurfaceView?.setOnClickListener {
                if (!volumeSliderVisible.value)
                    appBarVisible.value = !appBarVisible.value
                else
                    volumeSliderVisible.value = false
            }
        }

        exoPlayer.prepare()

        onDispose {
            exoPlayer.release()
        }
    }

    VideoPlayer(
        playerView = playerView,
        onPress = {
            if (!volumeSliderVisible.value)
                appBarVisible.value = !appBarVisible.value
            else
                volumeSliderVisible.value = false
        },
        onLongPress = {
            scope.launch {
                appBarVisible.value = true
                volumeSliderVisible.value = false
                sheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
    )

    VideoControls(
        player = exoPlayer,
        controlsVisible = appBarVisible.value,
        volumeSliderVisible = volumeSliderVisible,
        isVideoHasAudio = isVideoHasAudio,
    )
}