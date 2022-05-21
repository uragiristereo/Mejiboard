package com.github.uragiristereo.mejiboard.presentation.image.more.route.share

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.LocalMoreNavigation
import com.github.uragiristereo.mejiboard.presentation.image.more.route.core.SheetItem
import com.github.uragiristereo.mejiboard.presentation.main.LocalMainViewModel
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
                viewModel.trackShare(instance)
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