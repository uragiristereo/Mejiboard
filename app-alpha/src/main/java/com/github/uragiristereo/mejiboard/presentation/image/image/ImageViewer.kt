package com.github.uragiristereo.mejiboard.presentation.image.image

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import coil.request.Disposable
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun ImageViewer(
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel,
    post: Post,
    imageViewer: TouchImageView,
    imageDisposable: Disposable?,
    imageLoading: MutableState<Boolean>,
    appBarVisible: MutableState<Boolean>,
    sheetState: ModalBottomSheetState,
    onBackRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var originalImageDisposable: Disposable? = null
    val screenHeight = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val maxOffset = screenHeight * 0.17f
    val animatedOffsetY by animateFloatAsState(targetValue = imageViewModel.offsetY)
    var fingerCount by remember { mutableStateOf(1) }

    DisposableEffect(key1 = Unit) {
        onDispose {
            originalImageDisposable?.dispose()
        }
    }

    DisposableEffect(key1 = animatedOffsetY) {
        scope.launch {
            imageViewModel.animatedOffsetY = animatedOffsetY
        }

        onDispose { }
    }

    DisposableEffect(key1 = imageViewModel.isPressed) {
        scope.launch {
            if (!imageViewModel.isPressed) {
                if (abs(imageViewModel.offsetY) >= maxOffset * 0.7f) {
                    onBackRequest()
                } else {
                    imageViewModel.offsetY = 0f
                }
            }
        }

        onDispose { }
    }

    AndroidView(
        factory = { imageViewer },
        update = {
            if (imageViewModel.showOriginalImage && !imageViewModel.originalImageShown) {
                imageViewModel.originalImageShown = true

                imageDisposable?.dispose()
                originalImageDisposable = it.load(
                    ImageHelper.parseImageUrl(
                        post = post,
                        original = true
                    ),
                    imageLoader = mainViewModel.imageLoader,
                    builder = {
                        val resized = ImageHelper.resizeImage(post = post)

                        size(
                            width = resized.first,
                            height = resized.second,
                        )

                        listener(
                            onStart = { imageLoading.value = true },
                            onSuccess = { _, _ ->
                                imageLoading.value = false
                            }
                        )
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                forEachGesture {
                    val context = currentCoroutineContext()

                    awaitPointerEventScope {
                        do {
                            val event = awaitPointerEvent()

                            fingerCount = event.changes.size
                        } while (event.changes.any { it.pressed } && context.isActive)
                    }
                }
            }
            .pointerInput(
                key1 = imageViewModel.currentZoom,
                key2 = fingerCount,
            ) {
                if (imageViewModel.offsetY != 0f || fingerCount > 1) {
                    imageViewModel.isPressed = false
                }

                if (imageViewer.currentZoom == 1f) {
                    detectDragGestures(
                        onDragStart = { imageViewModel.isPressed = true },
                        onDragEnd = { imageViewModel.isPressed = false },
                        onDragCancel = { imageViewModel.isPressed = false },
                        onDrag = { change, dragAmount ->
                            if ((fingerCount == 1 || imageViewModel.offsetY != 0f) && sheetState.targetValue == ModalBottomSheetValue.Hidden) {
                                change.consumeAllChanges()

                                if (abs(imageViewModel.offsetY + dragAmount.y) <= maxOffset) {
                                    imageViewModel.offsetY += dragAmount.y
                                }
                            }
                        }
                    )
                }
            }
            .offset {
                IntOffset(
                    x = 0,
                    y =
                    if (imageViewModel.isPressed)
                        imageViewModel.offsetY.roundToInt()
                    else
                        animatedOffsetY.roundToInt(),
                )
            },
    )

    if (imageLoading.value) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                appBarVisible.value = true
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
}