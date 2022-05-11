package com.github.uragiristereo.mejiboard.presentation.image

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.common.extension.hideSystemBars
import com.github.uragiristereo.mejiboard.common.extension.showSystemBars
import com.github.uragiristereo.mejiboard.data.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageAppBar
import com.github.uragiristereo.mejiboard.presentation.image.image.ImagePost
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreBottomSheet
import com.github.uragiristereo.mejiboard.presentation.image.video.VideoPost
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.io.File


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun ImageScreen(
    post: Post,
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: ImageViewModel = hiltViewModel(),
) {
    remember {
        viewModel.state.update {
            it.copy(selectedPost = post)
        }
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val window = (context as Activity).window
    val systemUiController = rememberSystemUiController()
    val preferences = mainViewModel.preferences

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val imageType = remember { File(post.image).extension }

    BackHandler(enabled = sheetState.isVisible) {
        scope.launch { sheetState.hide() }
    }

    DisposableEffect(key1 = Unit) {
        viewModel.state.update {
            it.copy(
                showOriginalImage = preferences.previewSize == PreviewSize.Original,
            )
        }

        onDispose {
            val tempDirectory = File("${context.cacheDir.absolutePath}/temp/")

            window.showSystemBars()
            scope.launch { tempDirectory.deleteRecursively() }
        }
    }

    DisposableEffect(key1 = viewModel) {
        systemUiController.setSystemBarsColor(Color.Black.copy(alpha = 0.4f))

        onDispose { }
    }

    LaunchedEffect(key1 = viewModel.state.value.appBarVisible) {
        if (viewModel.state.value.appBarVisible)
            window.showSystemBars()
        else
            window.hideSystemBars()
    }

    if (imageType in Constants.SUPPORTED_TYPES_ANIMATION) {
        VideoPost(
            state = viewModel.state.value,
            sheetState = sheetState,
            okHttpClient = mainViewModel.okHttpClient,
            videoVolume = preferences.videoVolume,
            onVideoVolumeChange = {
                mainViewModel.updatePreferences(preferences.copy(videoVolume = it))
            },
        )
    }

    if (imageType in Constants.SUPPORTED_TYPES_IMAGE) {
        ImagePost(
            state = viewModel.state.value,
            imageLoader = mainViewModel.imageLoader,
            sheetState = sheetState,
            previewSize = preferences.previewSize,
            onBackRequest = {
                mainViewModel.backPressedByGesture = true
                mainNavigation.navigateUp()
            },
        )
    }

    ImageAppBar(
        state = viewModel.state.value,
        mainNavigation = mainNavigation,
        sheetState = sheetState,
        onShowImageChange = { new ->
            viewModel.state.update { it.copy(showOriginalImage = new) }
        },
    )

    MoreBottomSheet(sheetState = sheetState)
}