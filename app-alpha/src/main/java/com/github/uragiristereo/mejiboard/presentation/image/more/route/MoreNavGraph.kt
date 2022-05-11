package com.github.uragiristereo.mejiboard.presentation.image.more.route

import android.content.ClipData
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.github.uragiristereo.mejiboard.presentation.main.LocalMainViewModel
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import java.io.File

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
    val context = LocalContext.current
    val moreNavigation = rememberMaterialMotionNavController()
    val mainViewModel = LocalMainViewModel.current
    val state by viewModel.state

    remember {
        viewModel.state.update { it.copy(selectedPost = imageViewModel.state.value.selectedPost) }
        viewModel.parseImageUrls()
    }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            moreNavigation.navigateUp()
        }
    }

    LaunchedEffect(key1 = state.shareDownloadInfo) {
        if (state.shareDownloadInfo.status == "completed") {
            mainViewModel.removeInstance(state.selectedPost!!.id)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(state.shareDownloadInfo.path))

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                clipData = ClipData.newRawUri(null, uri)
                putExtra(Intent.EXTRA_STREAM, uri)
                type = context.contentResolver.getType(uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            ContextCompat.startActivity(context, Intent.createChooser(shareIntent, "Send to"), null)
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