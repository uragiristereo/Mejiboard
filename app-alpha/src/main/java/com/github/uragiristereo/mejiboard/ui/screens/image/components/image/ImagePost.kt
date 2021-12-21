package com.github.uragiristereo.mejiboard.ui.screens.image.components.image

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import coil.decode.GifDecoder
import coil.load
import coil.request.Disposable
import com.github.uragiristereo.mejiboard.model.network.Post
import com.github.uragiristereo.mejiboard.ui.screens.image.ImageViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.ImageHelper
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun ImagePost(
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel,
    post: Post,
    appBarVisible: MutableState<Boolean>,
    sheetState: ModalBottomSheetState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageViewer = remember { TouchImageView(context) }
    var imageDisposable: Disposable? = remember { null }
    val imageType = remember { File(post.image).extension }
    val imageLoading = remember { mutableStateOf(true) }

    DisposableEffect(key1 = Unit) {
        imageViewer.apply {
            maxZoom = 5f
            doubleTapScale = 2f

            setOnClickListener { appBarVisible.value = !appBarVisible.value }
            setOnLongClickListener {
                scope.launch {
                    appBarVisible.value = true
                    sheetState.animateTo(ModalBottomSheetValue.Expanded)
                }
                return@setOnLongClickListener false
            }

            imageDisposable = load(
                uri = ImageHelper.parseImageUrl(
                    post = post,
                    original = mainViewModel.previewSize == "original"
                ),
                imageLoader = mainViewModel.imageLoader,
                builder = {
                    if (imageType == "gif")
                        decoder(GifDecoder())

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

        onDispose {
            imageDisposable?.dispose()
        }
    }

    ImageViewer(
        mainViewModel = mainViewModel,
        imageViewModel = imageViewModel,
        post = post,
        imageViewer = imageViewer,
        imageDisposable = imageDisposable,
        imageLoading = imageLoading,
        appBarVisible = appBarVisible,
        sheetState = sheetState,
    )
}