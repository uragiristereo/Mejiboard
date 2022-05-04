package com.github.uragiristereo.mejiboard.presentation.image.image

import android.os.Build.VERSION.SDK_INT
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import coil.request.Disposable
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.data.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageState
import com.ortiz.touchview.OnTouchImageViewListener
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun ImagePost(
    state: ImageState,
    imageLoader: ImageLoader,
    previewSize: PreviewSize,
    sheetState: ModalBottomSheetState,
    viewModel: ImageViewModel = hiltViewModel(),
    onBackRequest: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageViewer = remember { TouchImageView(context) }
    var imageDisposable: Disposable? = remember { null }
    val post = state.selectedPost!!
    val imageType = remember { File(post.image).extension }

    DisposableEffect(key1 = Unit) {
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

            imageDisposable = load(
                uri = ImageHelper.parseImageUrl(
                    post = post,
                    original = previewSize == PreviewSize.Original,
                ),
                imageLoader = imageLoader,
                builder = {
                    if (imageType == "gif") {
                        if (SDK_INT >= 28) {
                            decoder(ImageDecoderDecoder(context))
                        } else {
                            decoder(GifDecoder())
                        }
                    }

                    val resized = ImageHelper.resizeImage(post = post)

                    size(width = resized.first, height = resized.second)

                    listener(
                        onStart = {
                            viewModel.state.update { it.copy(imageLoading = true) }
                        },
                        onSuccess = { _, _ ->
                            viewModel.state.update { it.copy(imageLoading = false) }
                        },
                    )
                }
            )
        }

        onDispose {
            imageDisposable?.dispose()
        }
    }

    ImageViewer(
        state = state,
        imageViewer = imageViewer,
        imageLoader = imageLoader,
        sheetState = sheetState,
        onBackRequest = onBackRequest,
        onImageDispose = { imageDisposable?.dispose() }
    )
}