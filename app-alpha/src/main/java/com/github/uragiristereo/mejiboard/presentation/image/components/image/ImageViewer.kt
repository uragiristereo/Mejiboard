package com.github.uragiristereo.mejiboard.presentation.image.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import coil.request.Disposable
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.launch

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
) {
    val scope = rememberCoroutineScope()
    var originalImageDisposable: Disposable? = null

    DisposableEffect(key1 = Unit) {
        onDispose {
            originalImageDisposable?.dispose()
        }
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