package com.github.uragiristereo.mejiboard.presentation.image.image

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.data.model.local.preferences.PreviewSize
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageState
import com.ortiz.touchview.OnTouchImageViewListener
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ImagePost(
    state: ImageState,
    previewSize: String,
    sheetState: ModalBottomSheetState,
    viewModel: ImageViewModel = hiltViewModel(),
    onBackRequest: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageViewer = remember { TouchImageView(context) }
    var imageDisposable: Disposable? = remember { null }
    val post = state.selectedPost!!

    val imageRequestTemplate = remember {
        ImageRequest.Builder(context)
            .target(imageViewer)
            .listener(
                onStart = {
                    viewModel.state.update { it.copy(imageLoading = true) }
                },
                onSuccess = { _, _ ->
                    viewModel.state.update { it.copy(imageLoading = false) }
                },
            )
    }

    val imageRequest = remember {
        when (post.originalImage.fileType) {
            "gif" -> imageRequestTemplate
                .decoderFactory(
                    factory = when {
                        SDK_INT >= 28 -> ImageDecoderDecoder.Factory()
                        else -> GifDecoder.Factory()
                    }
                )
            else -> imageRequestTemplate
        }
    }

    DisposableEffect(key1 = Unit) {
        val resized = ImageHelper.resizeImage(postImage = post.originalImage)

        imageDisposable = context.imageLoader.enqueue(
            request = imageRequest
                .data(
                    data = when {
                        previewSize == PreviewSize.Original -> post.originalImage.url
                        post.scaled -> post.scaledImage.url
                        else -> post.originalImage.url
                    },
                )
                .size(width = resized.first, height = resized.second)
                .build(),
        )

        imageViewer.apply {
            maxZoom = 5f
            doubleTapScale = 2f

            setOnClickListener {
                viewModel.state.update { it.copy(appBarVisible = !it.appBarVisible) }
            }

            setOnLongClickListener {
                scope.launch {
                    viewModel.state.update { it.copy(appBarVisible = true) }
                    sheetState.animateTo(ModalBottomSheetValue.Expanded)
                }

                return@setOnLongClickListener false
            }

            setOnTouchImageViewListener(object : OnTouchImageViewListener {
                override fun onMove() {
                    viewModel.state.update { it.copy(currentZoom = imageViewer.currentZoom) }
                }
            })
        }

        onDispose {
            imageDisposable?.dispose()
        }
    }

    ImageViewer(
        state = state,
        imageViewer = imageViewer,
        imageRequest = imageRequest,
        sheetState = sheetState,
        onBackRequest = onBackRequest,
        onImageDispose = { imageDisposable?.dispose() }
    )
}