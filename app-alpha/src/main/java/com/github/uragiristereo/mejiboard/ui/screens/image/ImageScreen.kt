package com.github.uragiristereo.mejiboard.ui.screens.image

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.decode.GifDecoder
import coil.load
import coil.request.Disposable
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.ui.components.ThumbPill
import com.github.uragiristereo.mejiboard.ui.navigation.PostMoreNavigation
import com.github.uragiristereo.mejiboard.ui.screens.image.components.VideoControls
import com.github.uragiristereo.mejiboard.ui.screens.image.components.VideoPlayer
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.hideSystemBars
import com.github.uragiristereo.mejiboard.util.showSystemBars
import com.google.accompanist.insets.*
import com.google.accompanist.insets.ui.TopAppBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.launch
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import timber.log.Timber
import java.io.File


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun ImageScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel = hiltViewModel(),
) {
    val post = mainViewModel.selectedPost!!
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val window = (context as Activity).window

    var url by remember { mutableStateOf("") }
    var appBarVisible by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val supportedTypesImage = listOf("jpg", "jpeg", "png", "gif")
    val supportedTypesAnimation = listOf("webm", "mp4")
    val imageType = File(post.image).extension
    val moreNavigation = rememberMaterialMotionNavController()

    var imageDisposable: Disposable? = null
    var originalImageDisposable: Disposable? = null
    var loading by remember { mutableStateOf(false) }
    val maxSize = 4096f

    LaunchedEffect(Unit) {
        imageViewModel.imageSize = ""
        imageViewModel.originalImageSize = ""
        imageViewModel.infoData.value = listOf()
        imageViewModel.showOriginalImage = mainViewModel.previewSize == "original"
        imageViewModel.originalImageShown = false
        imageViewModel.showTagsIsCollapsed = true
        imageViewModel.originalImageUpdated = false
    }

    BackHandler(
        enabled = sheetState.isVisible
    ) {
        scope.launch {
            sheetState.hide()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Timber.i("disposed ${imageDisposable == null}")
            Timber.i("disposed ${originalImageDisposable == null}")
            window.showSystemBars()
            imageDisposable?.dispose()
            originalImageDisposable?.dispose()
            val tempDirectory = File(context.cacheDir.absolutePath + "/temp/")
            tempDirectory.deleteRecursively()
        }
    }

    if (!sheetState.isVisible && imageViewModel.shareModalVisible) {
        moreNavigation.navigateUp()
        imageViewModel.shareModalVisible = false
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.HalfExpanded && sheetState.isVisible) {
            launch {
                imageViewModel.showTagsIsCollapsed = true
                sheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
    }

    LaunchedEffect(appBarVisible) {
        if (appBarVisible)
            window.showSystemBars()
        else
            window.hideSystemBars()
    }

    val originalUrl = "https://img3.gelbooru.com/images/" + post.directory + "/" + post.hash + "." + imageType

    if (imageType in supportedTypesAnimation) {
        val videoUrl = remember { "https://video-cdn3.gelbooru.com/images/" + post.directory + "/" + post.hash + "." + imageType }
        val volumeSliderVisible = remember { mutableStateOf(false) }
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
            }


            playerView.apply {
                player = exoPlayer
                useController = false
                controllerAutoShow = false

                videoSurfaceView?.isHapticFeedbackEnabled = false
                videoSurfaceView?.setOnLongClickListener {
                    scope.launch {
                        appBarVisible = true
                        volumeSliderVisible.value = false
                        sheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                    return@setOnLongClickListener true
                }
                videoSurfaceView?.setOnClickListener {
                    if (!volumeSliderVisible.value)
                        appBarVisible = !appBarVisible
                    else
                        volumeSliderVisible.value = false
                }
            }

            exoPlayer.apply {
                volume = 0.5f
                prepare()
            }

            onDispose {
                exoPlayer.release()
            }
        }

        VideoPlayer(
            playerView = playerView,
            onPress = {
                if (!volumeSliderVisible.value)
                    appBarVisible = !appBarVisible
                else
                    volumeSliderVisible.value = false
            },
            onLongPress = {
                scope.launch {
                    appBarVisible = true
                    volumeSliderVisible.value = false
                    sheetState.animateTo(ModalBottomSheetValue.Expanded)
                }
            }
        )

        VideoControls(
            player = exoPlayer,
            controlsVisible = appBarVisible,
            volumeSliderVisible = volumeSliderVisible,
        )
    }

    url =
        if (post.sample == 1 && imageType != "gif")
            "https://img3.gelbooru.com/samples/" + post.directory + "/sample_" + post.hash + ".jpg"
        else
            "https://img3.gelbooru.com/images/" + post.directory + "/" + post.hash + "." + imageType

    if (imageType in supportedTypesImage) {
        val touchImageView = TouchImageView(context)

        LaunchedEffect(true) {
            touchImageView.apply {
                setOnClickListener {
                    appBarVisible = !appBarVisible
                }
                setOnLongClickListener {
                    scope.launch {
                        appBarVisible = true
                        sheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                    return@setOnLongClickListener false
                }

                maxZoom = 5f
                doubleTapScale = 2f

                imageDisposable = load(
                    uri = url,
                    imageLoader = mainViewModel.imageLoader,
                    builder = {
                        if (File(url).extension == "gif")
                            decoder(GifDecoder())

                        crossfade(true)

                        if (post.width > maxSize || post.height > maxSize) {
                            val scale =
                                if (post.width > post.height)
                                    maxSize.div(post.width)
                                else
                                    maxSize.div(post.height)

                            val scaledWidth = post.width * scale
                            val scaledHeight = post.height * scale

                            size(scaledWidth.toInt(), scaledHeight.toInt())
                        } else
                            size(post.width, post.height)

                        listener(
                            onStart = { loading = true },
                            onSuccess = { _, _ ->
                                loading = false
                            }
                        )
                    }
                )
            }
        }

        AndroidView(
            factory = {
                touchImageView
            },
            update = {
                if (imageViewModel.showOriginalImage && !imageViewModel.originalImageShown) {
                    imageViewModel.originalImageShown = true

                    imageDisposable?.dispose()
                    originalImageDisposable = it.load(
                        originalUrl,
                        imageLoader = mainViewModel.imageLoader,
                        builder = {
                            crossfade(true)

                            if (post.width > maxSize || post.height > maxSize) {
                                val scale =
                                    if (post.width > post.height)
                                        maxSize.div(post.width)
                                    else
                                        maxSize.div(post.height)

                                val scaledWidth = post.width * scale
                                val scaledHeight = post.height * scale

                                size(scaledWidth.toInt(), scaledHeight.toInt())
                            } else
                                size(post.width, post.height)

                            listener(
                                onStart = { loading = true },
                                onSuccess = { _, _ ->
                                    loading = false
                                }
                            )
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }

    if (loading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                appBarVisible = true
                                sheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    AnimatedVisibility(
        visible = appBarVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(82.dp)
                .background(
                    brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent)),
                    alpha = 0.5f
                )
        )
        TopAppBar(
            title = { Text("Post ${post.id}") },
            navigationIcon = {
                IconButton(onClick = {
                    mainNavigation.navigateUp()
                }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            actions = {
                if (post.sample == 1 && !imageViewModel.showOriginalImage && imageType != "gif") {
                    IconButton(
                        onClick = {
                            imageViewModel.showOriginalImage = true
                        }
                    ) {
                        Icon(painterResource(R.drawable.open_in_full), "Show full size (original) image", tint = Color.White)
                    }
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            sheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    }
                ) {
                    Icon(Icons.Default.MoreVert, "Back", tint = Color.White)
                }
            },
            elevation = 0.dp,
            contentPadding = rememberInsetsPaddingValues(
                LocalWindowInsets.current.statusBars,
                applyBottom = false,
            ),
            backgroundColor = Color.Transparent,
            contentColor = Color.White
        )
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        scrimColor = Color(0xFF121212).copy(alpha = DrawerDefaults.ScrimOpacity),
        sheetContent = {
            Column(
                Modifier
                    .background(MaterialTheme.colors.background)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ThumbPill()
                }
                PostMoreNavigation(
                    imageViewModel,
                    sheetState,
                    post,
                    url,
                    originalUrl,
                    imageType,
                    moreNavigation,
                    mainViewModel
                )
                Box(Modifier.navigationBarsPadding())
            }
        },
    ) { }
}