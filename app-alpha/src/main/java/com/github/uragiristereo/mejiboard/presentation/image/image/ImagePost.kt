package com.github.uragiristereo.mejiboard.presentation.image.image

import android.os.Build.VERSION.SDK_INT
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import coil.request.Disposable
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.data.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.ortiz.touchview.OnTouchImageViewListener
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
    onBackRequest: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageViewer = remember { TouchImageView(context) }
    var imageDisposable: Disposable? = remember { null }
    val imageType = remember { File(post.image).extension }
    val imageLoading = remember { mutableStateOf(true) }
    val preferences = mainViewModel.preferences

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

            setOnTouchImageViewListener(object : OnTouchImageViewListener {
                override fun onMove() {
                    imageViewModel.currentZoom = imageViewer.currentZoom
                }
            })

            imageDisposable = load(
                uri = ImageHelper.parseImageUrl(
                    post = post,
                    original = preferences.previewSize == PreviewSize.Original
                ),
                imageLoader = mainViewModel.imageLoader,
                builder = {
                    if (imageType == "gif")
                        if (SDK_INT >= 28)
                            decoder(ImageDecoderDecoder(context))
                        else
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
        onBackRequest = onBackRequest,
    )
}