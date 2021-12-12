package com.github.uragiristereo.mejiboard.ui.screens.image

import android.app.Activity
import android.view.GestureDetector.OnDoubleTapListener
import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.github.uragiristereo.mejiboard.ui.viewmodel.ImageViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.hideSystemBars
import com.github.uragiristereo.mejiboard.util.showSystemBars
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.ui.TopAppBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
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

//    val originalUrl = "https://img3.gelbooru.com/images/" + post.directory + "/" + post.image
    val originalUrl = "https://img3.gelbooru.com/images/" + post.directory + "/" + post.hash + "." + imageType

    if (imageType in supportedTypesAnimation) {
//        val videoUrl = "https://video-cdn3.gelbooru.com/images/" + post.directory + "/" + post.image
        val videoUrl = "https://video-cdn3.gelbooru.com/images/" + post.directory + "/" + post.hash + "." + imageType

        val exoPlayer =
            ExoPlayer.Builder(context)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(
                        OkHttpDataSource.Factory(
                            mainViewModel.okHttpClient
                        )
                    )
                )
                .build()

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        val playerView = PlayerView(context)
        val mediaItem = MediaItem.fromUri(videoUrl)

        exoPlayer.setMediaItem(mediaItem)
        playerView.player = exoPlayer
        playerView.setOnLongClickListener {
            scope.launch {
                if (!appBarVisible) {
                    window.showSystemBars()
                    appBarVisible = true
                }
                sheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
            return@setOnLongClickListener true
        }

        LaunchedEffect(true) {
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                if (!appBarVisible) {
                                    window.showSystemBars()
                                    appBarVisible = true
                                }
                                sheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = {
                    playerView
                },
                update = {

                },
                modifier = Modifier
                    .navigationBarsPadding()
            )
        }
    }

//    url =
//        if (post.sample == 1 && imageType != "gif")
//            "https://img3.gelbooru.com/samples/" + post.directory + "/sample_" + post.image.replace(imageType, "jpg")
//        else
//            "https://img3.gelbooru.com/images/" + post.directory + "/" + post.image

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
                    if (appBarVisible)
                        window.hideSystemBars()
                    else
                        window.showSystemBars()

                    appBarVisible = !appBarVisible
                }
                setOnLongClickListener {
                    scope.launch {
                        if (!appBarVisible) {
                            window.showSystemBars()
                            appBarVisible = true
                        }
                        sheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                    return@setOnLongClickListener false
                }

                maxZoom = 5f
                doubleTapScale = 2f

                setOnDoubleTapListener(object : OnDoubleTapListener {
                    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                        return true
                    }

                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        return false
                    }

                    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                        return false
                    }
                })

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
                                if (!appBarVisible) {
                                    window.showSystemBars()
                                    appBarVisible = true
                                }
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