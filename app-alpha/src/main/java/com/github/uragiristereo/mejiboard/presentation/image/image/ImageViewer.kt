package com.github.uragiristereo.mejiboard.presentation.image.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageState
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ImageViewer(
    state: ImageState,
    imageViewer: TouchImageView,
    imageRequest: ImageRequest.Builder,
    sheetState: ModalBottomSheetState,
    viewModel: ImageViewModel = hiltViewModel(),
    onBackRequest: () -> Unit,
    onImageDispose: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var originalImageDisposable: Disposable? = null
    val screenHeight = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val maxOffset = screenHeight * 0.17f
    val animatedOffsetY by animateFloatAsState(targetValue = state.offsetY)
    val post = state.selectedPost!!

    DisposableEffect(key1 = Unit) {
        onDispose {
            originalImageDisposable?.dispose()
        }
    }

    DisposableEffect(key1 = animatedOffsetY) {
        scope.launch {
            viewModel.state.update { it.copy(animatedOffsetY = animatedOffsetY) }
        }

        onDispose { }
    }

    DisposableEffect(key1 = state.isPressed) {
        scope.launch {
            if (!state.isPressed) {
                if (abs(state.offsetY) >= maxOffset * 0.7f) {
                    onBackRequest()
                } else {
                    viewModel.state.update { it.copy(offsetY = 0f) }
                }
            }
        }

        onDispose { }
    }

    DisposableEffect(
        keys = arrayOf(
            state.showOriginalImage,
            state.originalImageShown,
        ),
    ) {
        scope.launch {
            if (state.showOriginalImage && !state.originalImageShown) {
                val resized = ImageHelper.resizeImage(post = post)

                viewModel.state.update {
                    it.copy(
                        originalImageShown = true,
                        imageLoading = true
                    )
                }

                delay(timeMillis = 150L)

                onImageDispose()

                originalImageDisposable = context.imageLoader.enqueue(
                    request = imageRequest
                        .data(
                            data = ImageHelper.parseImageUrl(
                                post = post,
                                original = true,
                            ),
                        )
                        .size(width = resized.first, height = resized.second)
                        .build(),
                )
            }
        }

        onDispose { }
    }

    AndroidView(
        factory = { imageViewer },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .pointerInput(key1 = Unit) {
                forEachGesture {
                    val coroutineContext = currentCoroutineContext()

                    awaitPointerEventScope {
                        do {
                            val event = awaitPointerEvent()

                            viewModel.state.update { it.copy(fingerCount = event.changes.size) }
                        } while (event.changes.any { it.pressed } && coroutineContext.isActive)
                    }
                }
            }
            .pointerInput(
                key1 = state.currentZoom,
                key2 = state.fingerCount,
            ) {
                if (state.offsetY != 0f || state.fingerCount > 1) {
                    viewModel.state.update { it.copy(isPressed = false) }
                }

                if (imageViewer.currentZoom == 1f) {
                    detectDragGestures(
                        onDragStart = {
                            viewModel.state.update { it.copy(isPressed = true) }
                        },
                        onDragEnd = {
                            scope.launch {
                                delay(timeMillis = 50L)

                                viewModel.state.update { it.copy(isPressed = false) }
                            }
                        },
                        onDragCancel = {
                            viewModel.state.update { it.copy(isPressed = false) }
                        },
                        onDrag = { change, dragAmount ->
                            if ((state.fingerCount == 1 || viewModel.state.value.offsetY != 0f) && sheetState.targetValue == ModalBottomSheetValue.Hidden) {
                                change.consumeAllChanges()

                                val deceleratedDragAmount = dragAmount.y * 0.7f

                                if (abs(viewModel.state.value.offsetY + deceleratedDragAmount) <= maxOffset) {
                                    viewModel.state.update { it.copy(offsetY = it.offsetY + deceleratedDragAmount) }
                                }
                            }
                        }
                    )
                }
            }
            .offset(
                y = when {
                    state.isPressed || abs(state.offsetY) >= maxOffset * 0.7f -> with(density) { state.offsetY.toDp() }
                    else -> with(density) { animatedOffsetY.toDp() }
                },
            ),
    )

    AnimatedVisibility(
        visible = state.imageLoading,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .pointerInput(key1 = Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                viewModel.state.update { it.copy(appBarVisible = true) }
                                sheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    )
                },
            content = { CircularProgressIndicator() },
        )
    }
}