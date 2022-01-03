package com.github.uragiristereo.mejiboard.presentation.more

import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.data.model.DownloadInfo
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.common.SheetItem
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun MoreShare(
    post: Post,
    moreNavigation: NavHostController,
    sheetState: ModalBottomSheetState,
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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


    var shareDownloadInfo by remember { mutableStateOf(DownloadInfo()) }
    var shareDownloadSpeed by remember { mutableStateOf(0) }
    var dialogShown by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        imageViewModel.shareModalVisible = true
    }

    Column {
        SheetItem(
            text = "Back",
            icon = { Icon(painterResource(R.drawable.arrow_back_ios), "Share") },
            onClick = {
                moreNavigation.navigateUp()
            }
        )
        Divider()
        Text(
            "Share",
            Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 4.dp
                ),
            style = MaterialTheme.typography.h6
        )

        val postLinkIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://gelbooru.com/index.php?page=post&s=view&id=${post.id}")
            type = "text/plain"
        }

        SheetItem(
            content = {
                Column {
                    Text("Post link")
                    Text(
                        "https://gelbooru.com/index.php?page=post&s=view&id=${post.id}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(0.5f)
                    )

                }
            },
            icon = { Icon(painterResource(R.drawable.link), "Share") },
            onClick = {
                scope.launch {
                    sheetState.hide()
                    context.startActivity(Intent.createChooser(postLinkIntent, null))
                }
            }
        )

        if (post.sample == 1 && imageType != "gif") {
            SheetItem(
                content = {
                    Column {
                        Text("Compressed (sample) image")
                        Text(
                            "Resolution: ${post.sampleWidth}x${post.sampleHeight} Size: ${imageViewModel.imageSize}",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(0.5f)
                        )

                    }
                },
                icon = { Icon(painterResource(R.drawable.aspect_ratio), "Share") },
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        val tempDir = File(context.cacheDir.absolutePath + "/temp/")
                        if (!tempDir.isDirectory)
                            tempDir.mkdir()

                        val instance = mainViewModel.newDownloadInstance(context, post.id, imageUrl, tempDir)

                        if (instance == null) {
                            Toast.makeText(context, "Error: Image is already in download queue.", Toast.LENGTH_LONG).show()
                        } else {
                            dialogShown = true
                            var lastDownloaded: Long
                            while (instance.info.status == "downloading") {
                                lastDownloaded = instance.info.downloaded
                                delay(1000)
                                shareDownloadInfo = instance.info
                                shareDownloadSpeed = (instance.info.downloaded - lastDownloaded).toInt()
                            }
                            dialogShown = false
                            if (instance.info.status == "completed") {
                                mainViewModel.removeInstance(post.id)
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(instance.info.path))

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
                    }
                }
            )
        }

        val originalImageSize = if (post.sample == 1 && imageType != "gif") imageViewModel.originalImageSize else imageViewModel.imageSize

        SheetItem(
            content = {
                Column {
                    Text("Full size (original) image")
                    Text(
                        "Resolution: ${post.width}x${post.height} Size: $originalImageSize",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(0.5f)
                    )

                }
            },
            icon = { Icon(painterResource(R.drawable.open_in_full), "Share") },
            onClick = {
                scope.launch {
                    sheetState.hide()
                    val tempDir = File(context.cacheDir.absolutePath + "/temp/")
                    if (!tempDir.isDirectory)
                        tempDir.mkdir()

                    val instance = mainViewModel.newDownloadInstance(context, post.id, originalImageUrl, tempDir)

                    if (instance == null) {
                        Toast.makeText(context, "Error: Image is already in download queue.", Toast.LENGTH_LONG).show()
                    } else {
                        dialogShown = true
                        var lastDownloaded: Long
                        while (instance.info.status == "downloading") {
                            lastDownloaded = instance.info.downloaded
                            delay(1000)
                            shareDownloadInfo = instance.info
                            shareDownloadSpeed = (instance.info.downloaded - lastDownloaded).toInt()
                        }
                        dialogShown = false
                        if (instance.info.status == "completed") {
                            mainViewModel.removeInstance(post.id)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(instance.info.path))

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
                }
            }
        )
    }
}