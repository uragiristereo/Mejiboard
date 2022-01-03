package com.github.uragiristereo.mejiboard.presentation.image

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.extension.showSystemBars
import com.github.uragiristereo.mejiboard.presentation.image.components.core.ImageAppBar
import com.github.uragiristereo.mejiboard.presentation.image.components.core.ImageBottomSheet
import com.github.uragiristereo.mejiboard.presentation.image.components.image.ImagePost
import com.github.uragiristereo.mejiboard.presentation.image.components.video.VideoPost
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import kotlinx.coroutines.launch
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

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val appBarVisible = remember { mutableStateOf(true) }

    val supportedTypesImage = remember { listOf("jpg", "jpeg", "png", "gif") }
    val supportedTypesAnimation = remember { listOf("webm", "mp4") }
    val imageType = remember { File(post.image).extension }

    BackHandler(enabled = sheetState.isVisible) {
        scope.launch { sheetState.hide() }
    }

    DisposableEffect(key1 = Unit) {
        imageViewModel.showOriginalImage = mainViewModel.previewSize == "original"

        onDispose {
            val tempDirectory = File("${context.cacheDir.absolutePath}/temp/")

            window.showSystemBars()
            tempDirectory.deleteRecursively()
        }
    }

    if (imageType in supportedTypesAnimation) {
        VideoPost(
            mainViewModel = mainViewModel,
            imageViewModel = imageViewModel,
            post = post,
            appBarVisible = appBarVisible,
            sheetState = sheetState,
        )
    }

    if (imageType in supportedTypesImage) {
        ImagePost(
            mainViewModel = mainViewModel,
            imageViewModel = imageViewModel,
            post = post,
            appBarVisible = appBarVisible,
            sheetState = sheetState,
        )
    }

    ImageAppBar(
        imageViewModel = imageViewModel,
        post = post,
        appBarVisible = appBarVisible,
        mainNavigation = mainNavigation,
        sheetState = sheetState,
    )

    ImageBottomSheet(
        mainViewModel = mainViewModel,
        imageViewModel = imageViewModel,
        post = post,
        sheetState = sheetState,
    )
}