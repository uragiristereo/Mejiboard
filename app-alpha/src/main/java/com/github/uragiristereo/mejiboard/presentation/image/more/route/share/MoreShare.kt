package com.github.uragiristereo.mejiboard.presentation.image.more.route.share

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
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.LocalImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.LocalMoreNavigation
import com.github.uragiristereo.mejiboard.presentation.image.more.route.core.SheetItem
import com.github.uragiristereo.mejiboard.presentation.main.LocalMainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Composable
fun MoreShare(
    sheetState: ModalBottomSheetState,
    viewModel: MoreViewModel,
) {
    val context = LocalContext.current
    val mainViewModel = LocalMainViewModel.current
    val imageViewModel = LocalImageViewModel.current
    val moreNavigation = LocalMoreNavigation.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state

    val post = remember { state.selectedPost!! }
    val imageType = remember { File(post.image).extension }

    fun shareImage(url: String) {
        scope.launch {
            sheetState.hide()

            val tempDir = File(context.cacheDir.absolutePath + "/temp/")
            if (!tempDir.isDirectory)
                tempDir.mkdir()

            val instance = mainViewModel.newDownloadInstance(
                context = context,
                postId = post.id,
                url = url,
                location = tempDir,
            )

            if (instance == null) {
                Toast.makeText(context, "Error: Image is already in download queue.", Toast.LENGTH_LONG).show()
            } else {
                viewModel.state.update { it.copy(dialogShown = true) }

                var lastDownloaded: Long

                while (instance.info.status == "downloading") {
                    lastDownloaded = instance.info.downloaded

                    delay(1000)

                    viewModel.state.update {
                        it.copy(
                            shareDownloadInfo = instance.info,
                            shareDownloadSpeed = instance.info.downloaded - lastDownloaded,
                        )
                    }
                }

                viewModel.state.update { it.copy(dialogShown = true) }

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

    Column {
        SheetItem(
            text = "Back",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back_ios),
                    contentDescription = null,
                )
            },
            onClick = {
                moreNavigation.navigateUp()
            }
        )

        Divider()

        Text(
            text = "Share",
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 4.dp,
                ),
            style = MaterialTheme.typography.h6,
        )

        val postLinkIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://gelbooru.com/index.php?page=post&s=view&id=${post.id}")
            type = "text/plain"
        }

        SheetItem(
            title = "Post link",
            subtitle = "https://gelbooru.com/index.php?page=post&s=view&id=${post.id}",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.link),
                    contentDescription = "Share",
                )
            },
            onClick = {
                scope.launch {
                    sheetState.hide()
                    context.startActivity(Intent.createChooser(postLinkIntent, null))
                }
            }
        )

        if (post.sample == 1 && imageType != "gif") {
            SheetItem(
                title = "Compressed (sample) image",
                subtitle = "Resolution: ${post.sampleWidth}x${post.sampleHeight} Size: ${state.imageSize}",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.aspect_ratio),
                        contentDescription = null,
                    )
                },
                onClick = {
                    shareImage(url = state.imageUrl)
                }
            )
        }

        val originalImageSize = when {
            post.sample == 1 && imageType != "gif" -> state.originalImageSize
            else -> state.imageSize
        }

        SheetItem(
            title = "Full size (original) image",
            subtitle = "Resolution: ${post.width}x${post.height} Size: $originalImageSize",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.open_in_full),
                    contentDescription = null,
                )
            },
            onClick = {
                shareImage(url = state.originalImageUrl)
            }
        )
    }
}