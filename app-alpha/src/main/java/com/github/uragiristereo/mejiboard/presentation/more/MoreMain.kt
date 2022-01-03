package com.github.uragiristereo.mejiboard.presentation.more

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.common.helper.PermissionHelper
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.common.SheetItem
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun MoreMain(
    post: Post,
    sheetState: ModalBottomSheetState,
    moreNavigation: NavHostController,
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imageType = remember { File(post.image).extension }
    val imageUrl = remember {
        ImageHelper.parseImageUrl(
            post = post,
            original = false,
        )
    }
    val originalImageUrl = remember {
        ImageHelper.parseImageUrl(
            post = post,
            original = true,
        )
    }

    Column {
        SheetItem(
            text = "Post info",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                )
            },
            onClick = {
                if (imageViewModel.imageSize.isEmpty())
                    imageViewModel.checkImage(imageUrl, original = false)

                if (post.sample == 1 && imageViewModel.originalImageSize.isEmpty() && imageType != "gif")
                    imageViewModel.checkImage(originalImageUrl, original = true)

                imageViewModel.showTagsIsCollapsed = true

                moreNavigation.navigate("info")
            }
        )
        if (post.sample == 1 && !imageViewModel.showOriginalImage && imageType != "gif") {
            SheetItem(
                text = "Show full size (original) image",
                icon = { Icon(painterResource(R.drawable.open_in_full), "Show full size (original) image") },
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        imageViewModel.showOriginalImage = true
                    }
                }
            )
        }
        SheetItem(
            text = "Save to device",
            icon = { Icon(painterResource(R.drawable.file_download), "Save") },
            onClick = {
                scope.launch {
                    sheetState.hide()

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
                    if (!downloadLocation.isDirectory)
                        downloadLocation.mkdir()

                    val instance = mainViewModel.newDownloadInstance(context, post.id, originalImageUrl, downloadLocation)

                    if (instance == null) {
                        Toast.makeText(context, "Error: Image is already in download queue.", Toast.LENGTH_LONG).show()
                    } else {
                        mainViewModel.trackDownloadProgress(
                            context,
                            post,
                            instance
                        )
                    }
                }
            }
        )
        SheetItem(
            text = "Open post in browser",
            icon = {
                Icon(
                    painterResource(R.drawable.open_in_browser),
                    "Open post in browser"
                )
            },
            onClick = {
                scope.launch {
                    sheetState.hide()
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gelbooru.com/index.php?page=post&s=view&id=${post.id}"))
                context.startActivity(intent)
            }
        )
        SheetItem(
            text = "Share...",
            icon = { Icon(Icons.Outlined.Share, "Share") },
            onClick = {
                if (imageViewModel.imageSize.isEmpty())
                    imageViewModel.checkImage(imageUrl, original = false)

                if (post.sample == 1 && imageViewModel.originalImageSize.isEmpty() && imageType != "gif")
                    imageViewModel.checkImage(originalImageUrl, original = true)

                moreNavigation.navigate("share") {
                    popUpTo("main") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}