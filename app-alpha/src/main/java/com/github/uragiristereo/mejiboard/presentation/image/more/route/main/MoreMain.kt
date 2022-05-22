package com.github.uragiristereo.mejiboard.presentation.image.more.route.main

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.helper.PermissionHelper
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.LocalImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.LocalMoreNavigation
import com.github.uragiristereo.mejiboard.presentation.image.more.route.core.MoreRoute
import com.github.uragiristereo.mejiboard.presentation.image.more.route.core.SheetItem
import com.github.uragiristereo.mejiboard.presentation.main.LocalMainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun MoreMain(
    sheetState: ModalBottomSheetState,
    viewModel: MoreViewModel,
) {
    val moreNavigation = LocalMoreNavigation.current
    val mainViewModel = LocalMainViewModel.current
    val imageViewModel = LocalImageViewModel.current
    val state by viewModel.state

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val post = state.selectedPost!!

    fun checkImageAndNavigate(route: String) {
        if (post.scaled && state.imageSize.isEmpty()) {
            viewModel.checkImage(original = false)
        }

        if (state.originalImageSize.isEmpty()) {
            viewModel.checkImage(original = true)
        }

        viewModel.state.update { it.copy(isShowTagsCollapsed = true) }

        moreNavigation.navigate(route = route) {
            popUpTo(route = MoreRoute.Main) { saveState = true }

            launchSingleTop = true
            restoreState = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SheetItem(
            text = "Post info",
            icon = {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
            },
            onClick = {
                checkImageAndNavigate(route = MoreRoute.Info)
            },
        )

        if (post.scaled && !imageViewModel.state.value.showOriginalImage && post.originalImage.fileType != "gif") {
            SheetItem(
                text = "Show full size (original) image",
                icon = { Icon(painterResource(R.drawable.open_in_full), "Show full size (original) image") },
                onClick = {
                    scope.launch {
                        sheetState.hide()

                        imageViewModel.state.update { it.copy(showOriginalImage = true) }
                    }
                },
            )
        }

        SheetItem(
            text = "Save to device",
            icon = {
                Icon(painter = painterResource(id = R.drawable.file_download), contentDescription = null)
            },
            onClick = {
                scope.launch {
                    sheetState.hide()

                    // TODO: use better permission manager solution
                    // TODO: migrate to scoped storage

                    if (!PermissionHelper.checkPermission(context)) {
                        PermissionHelper.requestPermission(context)
                        mainViewModel.permissionState.value = "wait"
                        while (mainViewModel.permissionState.value == "wait") {
                            delay(500)
                        }
                        if (mainViewModel.permissionState.value == "denied") {
                            Toast.makeText(context, "Error: Storage permission is not granted", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                    }

                    Toast.makeText(context, "Download started.\nCheck notification for download progress.", Toast.LENGTH_SHORT).show()

                    val downloadLocation = File(Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Mejiboard/")

                    if (!downloadLocation.isDirectory) {
                        downloadLocation.mkdir()
                    }

                    val instance = mainViewModel.newDownloadInstance(
                        context = context,
                        postId = post.id,
                        url = state.originalImageUrl,
                        location = downloadLocation,
                    )

                    if (instance == null) {
                        Toast.makeText(context, "Error: Image is already in download queue.", Toast.LENGTH_LONG).show()
                    } else {
                        mainViewModel.trackDownloadProgress(
                            context = context,
                            post = post,
                            instance = instance,
                        )
                    }
                }
            }
        )

        SheetItem(
            text = "Open post in browser",
            icon = {
                Icon(painter = painterResource(id = R.drawable.open_in_browser), contentDescription = null)
            },
            onClick = {
                scope.launch { sheetState.hide() }

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ApiProviders.SafebooruOrg.parseWebUrl(post.id)))
                context.startActivity(intent)
            }
        )
        SheetItem(
            text = "Share...",
            icon = { Icon(Icons.Outlined.Share, "Share") },
            onClick = {
                checkImageAndNavigate(route = MoreRoute.Share)
            }
        )
    }
}