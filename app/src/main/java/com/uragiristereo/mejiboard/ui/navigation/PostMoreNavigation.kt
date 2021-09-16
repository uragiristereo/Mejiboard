package com.uragiristereo.mejiboard.ui.navigation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsPadding
import com.uragiristereo.mejiboard.R
import com.uragiristereo.mejiboard.model.Post
import com.uragiristereo.mejiboard.ui.components.SheetInfoItem
import com.uragiristereo.mejiboard.ui.components.SheetItem
import com.uragiristereo.mejiboard.ui.components.TagInfoItem
import com.uragiristereo.mejiboard.ui.viewmodel.ImageViewModel
import kotlinx.coroutines.launch
import soup.compose.material.motion.materialSharedAxisXIn
import soup.compose.material.motion.materialSharedAxisXOut
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import java.io.File
import java.util.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun PostMoreNavigation(
    imageViewModel: ImageViewModel,
    sheetState: ModalBottomSheetState,
    post: Post,
    url: String,
    originalUrl: String,
    imageType: String,
    moreNavigation: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    MaterialMotionNavHost(navController = moreNavigation, startDestination = "main") {
        composable("main") {
            Column {
                SheetItem(
                    text = "Post info",
                    icon = { Icon(Icons.Outlined.Info, "Info") },
                    onClick = {
                        if (imageViewModel.imageSize.isEmpty())
                            imageViewModel.checkImage(url, original = false)

                        if (post.sample == 1 && imageViewModel.originalImageSize.isEmpty() && imageType != "gif")
                            imageViewModel.checkImage(originalUrl, original = true)

                        imageViewModel.showTagsIsCollapsed = true

                        moreNavigation.navigate("info") {
                            popUpTo("main") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                            Toast.makeText(context, "Feature not yet available", Toast.LENGTH_SHORT).show()
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
                            imageViewModel.checkImage(url, original = false)

                        if (post.sample == 1 && imageViewModel.originalImageSize.isEmpty() && imageType != "gif")
                            imageViewModel.checkImage(originalUrl, original = true)

                        moreNavigation.navigate("share") {
                            popUpTo("main") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

        composable(
            "share",
            enterMotionSpec = { _, _ -> materialSharedAxisXIn() },
            exitMotionSpec = { _, _ -> materialSharedAxisXOut() },
        ) {
            imageViewModel.shareModalVisible = true
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
                                    "Resolution: ${post.sample_width}x${post.sample_height} Size: ${imageViewModel.imageSize}",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(0.5f)
                                )

                            }
                        },
                        icon = { Icon(painterResource(R.drawable.aspect_ratio), "Share") },
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                Toast.makeText(context, "Feature not yet available", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, "Feature not yet available", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        composable(
            "info",
        ) {
            imageViewModel.shareModalVisible = true
            Column {
                SheetItem(
                    text = "Close",
                    icon = {
                        Icon(Icons.Outlined.Close, "Close")
                    },
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            moreNavigation.navigateUp()
                        }
                    }
                )
                Divider()
                LazyColumn {
                    item {
                        Text(
                            "Info",
                            Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    bottom = 4.dp
                                ),
                            style = MaterialTheme.typography.h6
                        )
                    }
                    item {
                        SheetInfoItem(
                            "Post ID",
                            "${post.id}"
                        )
                    }
                    item {
                        SheetInfoItem(
                            "Date posted",
                            "${post.created_at}"
                        )
                    }
                    item {
                        SheetInfoItem(
                            "Uploader",
                            post.owner
                        )
                    }
                    val rating =
                        when (post.rating) {
                            "s" -> "Safe"
                            "q" -> "Questionable"
                            "e" -> "Explicit"
                            else -> "Safe"
                        }
                    item {
                        SheetInfoItem(
                            "Rating",
                            rating
                        )
                    }
                    if (post.source.isNotEmpty()) {
                        var source = post.source.replace("&amp;", "&")
                        source =
                            if ("https://" in source || "http://" in source)
                                source
                            else
                                "https://www.google.com/search?q=${source}"

                        item {
                            SheetInfoItem(
                                "Source",
                                source,
                                Modifier
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source))
                                        context.startActivity(intent)
                                    }
                            )
                        }
                    }
                    if (post.sample == 1 && imageType != "gif") {
                        item {
                            SheetInfoItem(
                                "Compressed (sample) image size",
                                "${post.sample_width}x${post.sample_height}\n${imageViewModel.imageSize} (jpg)"
                            )
                        }
                    }
                    val originalImageSize = if (post.sample == 1 && imageType != "gif") imageViewModel.originalImageSize else imageViewModel.imageSize

                    item {
                        SheetInfoItem(
                            "Full (original) image size",
                            "${post.width}x${post.height}\n$originalImageSize (${File(post.image).extension})"
                        )
                    }
                    item {
                        Divider(
                            Modifier
                                .padding(
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                        )
                    }
                    if (imageViewModel.showTagsIsCollapsed && !imageViewModel.infoProgressVisible) {
                        item {
                            SheetItem(
                                text = "Show tags",
                                icon = {
                                    Icon(painterResource(R.drawable.expand_more), "Close")
                                },
                                onClick = {
                                    if (imageViewModel.infoData.value.isEmpty())
                                        imageViewModel.getTagsInfo(post.tags)
                                    imageViewModel.showTagsIsCollapsed = false
                                }
                            )
                        }
                    }
                    if (imageViewModel.infoProgressVisible) {
                        item {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (imageViewModel.infoData.value.isNotEmpty() && !imageViewModel.infoProgressVisible && !imageViewModel.showTagsIsCollapsed) {
                        var artists = imageViewModel.infoData.value.filter { it.type == "artist" }
                        var characters =
                            imageViewModel.infoData.value.filter { it.type == "character" }
                        var copyrights =
                            imageViewModel.infoData.value.filter { it.type == "copyright" }
                        var metadatas =
                            imageViewModel.infoData.value.filter { it.type == "metadata" }
                        var tags = imageViewModel.infoData.value.filter { it.type == "tag" }

                        artists = artists.sortedBy { it.tag }
                        characters = characters.sortedBy { it.tag }
                        copyrights = copyrights.sortedBy { it.tag }
                        metadatas = metadatas.sortedBy { it.tag }
                        tags = tags.sortedBy { it.tag }

                        item {
                            Text(
                                "Tags",
                                Modifier
                                    .padding(
                                        start = 16.dp,
                                        top = 16.dp,
                                        bottom = 4.dp
                                    ),
                                style = MaterialTheme.typography.h6
                            )
                        }

                        if (artists.isNotEmpty()) {
                            item {
                                Text(
                                    "Artist".uppercase(Locale.getDefault()),
                                    Modifier
                                        .padding(
                                            start = 16.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    style = MaterialTheme.typography.overline,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            items(artists) { item ->
                                TagInfoItem(
                                    tag = item.tag,
                                    count = item.count
                                )
                            }
                        }

                        if (characters.isNotEmpty()) {
                            item {
                                Text(
                                    "Character".uppercase(Locale.getDefault()),
                                    Modifier
                                        .padding(
                                            start = 16.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    style = MaterialTheme.typography.overline,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            items(characters) { item ->
                                TagInfoItem(
                                    tag = item.tag,
                                    count = item.count
                                )
                            }
                        }

                        if (copyrights.isNotEmpty()) {
                            item {
                                Text(
                                    "Copyright".uppercase(Locale.getDefault()),
                                    Modifier
                                        .padding(
                                            start = 16.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    style = MaterialTheme.typography.overline,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            items(copyrights) { item ->
                                TagInfoItem(
                                    tag = item.tag,
                                    count = item.count
                                )
                            }
                        }

                        if (metadatas.isNotEmpty()) {
                            item {
                                Text(
                                    "Metadata".uppercase(Locale.getDefault()),
                                    Modifier
                                        .padding(
                                            start = 16.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    style = MaterialTheme.typography.overline,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            items(metadatas) { item ->
                                TagInfoItem(
                                    tag = item.tag,
                                    count = item.count
                                )
                            }
                        }

                        if (tags.isNotEmpty()) {
                            item {
                                Text(
                                    "Tag".uppercase(Locale.getDefault()),
                                    Modifier
                                        .padding(
                                            start = 16.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        ),
                                    style = MaterialTheme.typography.overline,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            items(tags) { item ->
                                TagInfoItem(
                                    tag = item.tag,
                                    count = item.count
                                )
                            }
                        }

                        item {
                            Box(Modifier.navigationBarsPadding())
                        }
                    }
                }
            }
        }
    }
}