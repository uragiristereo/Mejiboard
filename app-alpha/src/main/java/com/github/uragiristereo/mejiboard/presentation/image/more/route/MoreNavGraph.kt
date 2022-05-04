package com.github.uragiristereo.mejiboard.presentation.image.more.route

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.core.ShareDownloadDialog
import com.github.uragiristereo.mejiboard.presentation.image.more.route.core.MoreRoute
import com.github.uragiristereo.mejiboard.presentation.image.more.route.info.MoreInfo
import com.github.uragiristereo.mejiboard.presentation.image.more.route.main.MoreMain
import com.github.uragiristereo.mejiboard.presentation.image.more.route.share.MoreShare
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController

val LocalMoreNavigation = compositionLocalOf<NavHostController> { error("no More NavHostController provided!") }
val LocalImageViewModel = compositionLocalOf<ImageViewModel> { error("no ImageViewModel provided!") }

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MoreNavGraph(
    sheetState: ModalBottomSheetState,
    imageViewModel: ImageViewModel,
    viewModel: MoreViewModel = hiltViewModel(),
) {
    val moreNavigation = rememberMaterialMotionNavController()
    val state by viewModel.state

    remember {
        viewModel.state.update { it.copy(selectedPost = imageViewModel.state.value.selectedPost) }
    }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            moreNavigation.navigateUp()
        }
    }

    if (state.dialogShown) {
        ShareDownloadDialog(
            state = state,
            viewModel = viewModel,
        )
    }

    CompositionLocalProvider(
        values = arrayOf(
            LocalMoreNavigation provides moreNavigation,
            LocalImageViewModel provides imageViewModel,
        ),
        content = {
            MaterialMotionNavHost(
                navController = moreNavigation,
                startDestination = MoreRoute.Main,
            ) {
                composable(
                    route = MoreRoute.Main,
                    content = {
                        MoreMain(
                            sheetState = sheetState,
                            viewModel = viewModel,
                        )
                    },
                )

                composable(
                    route = MoreRoute.Info,
                    content = {
                        MoreInfo(
                            sheetState = sheetState,
                            viewModel = viewModel,
                        )
                    },
                )

                composable(
                    route = MoreRoute.Share,
                    content = {
                        MoreShare(
                            sheetState = sheetState,
                            viewModel = viewModel,
                        )
                    },
                )
            }
        }
    )
}