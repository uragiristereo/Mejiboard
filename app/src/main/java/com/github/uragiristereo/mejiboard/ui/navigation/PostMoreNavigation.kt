package com.github.uragiristereo.mejiboard.ui.navigation

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.model.network.DownloadInfo
import com.github.uragiristereo.mejiboard.model.network.Post
import com.github.uragiristereo.mejiboard.ui.components.SheetInfoItem
import com.github.uragiristereo.mejiboard.ui.components.SheetItem
import com.github.uragiristereo.mejiboard.ui.components.TagInfoItem
import com.github.uragiristereo.mejiboard.ui.viewmodel.ImageViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.FileHelper.convertSize
import com.github.uragiristereo.mejiboard.util.PermissionHelper
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import soup.compose.material.motion.materialSharedAxisXIn
import soup.compose.material.motion.materialSharedAxisXOut
import soup.compose.material.motion.materialSharedAxisZIn
import soup.compose.material.motion.materialSharedAxisZOut
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import java.io.File
import java.text.DateFormat
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
    moreNavigation: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var shareDownloadInfo by remember { mutableStateOf(DownloadInfo()) }
    var shareDownloadSpeed by remember { mutableStateOf(0) }
    var dialogShown by remember { mutableStateOf(false)  }

    if (dialogShown) {
        AlertDialog(
            onDismissRequest = { },
            buttons = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 16.dp,
                            bottom = 8.dp
                        ),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                dialogShown = false
                                val instance = mainViewModel.getInstance(post.id)
                                instance?.cancel()
                                mainViewModel.removeInstance(post.id)
                            }
                            .padding(8.dp)
                    ) {
                        Text(
                            "Cancel".uppercase(),
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            },
            title = {
                Text("Downloading...")
            },
            text = {
                val progressSmooth by animateFloatAsState(targetValue = shareDownloadInfo.progress)
                Column {
                    val progressFormatted = "%.2f".format(shareDownloadInfo.progress.times(100))

                    if (progressSmooth != 0f) {
                        LinearProgressIndicator(
                            progress = progressSmooth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$progressFormatted%")
                        Text("${convertSize(shareDownloadSpeed)}/s")
                    }
                    Text(
                        "${convertSize(shareDownloadInfo.downloaded.toInt())} / ${convertSize(shareDownloadInfo.length.toInt())}",
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        )
    }

    MaterialMotionNavHost(navController = moreNavigation, startDestination = "main") {
        composable(
            "main",
            enterMotionSpec = { initial, _ ->
                if (initial.destination.route == "share")
                    materialSharedAxisXIn()
                else
                    materialSharedAxisZIn()
            },
            exitMotionSpec = { initial, _ ->
                if (initial.destination.route == "share")
                    materialSharedAxisXOut()
                else
                    materialSharedAxisZOut()
            }
        ) {
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

                            if (!PermissionHelper.checkPermission(context)) {
                                PermissionHelper.requestPermission(context)
                                mainViewModel.setPermissionState("wait")
                                while (mainViewModel.getPermissionState() == "wait") {
                                    delay(500)
                                }
                                if (mainViewModel.getPermissionState() == "denied") {
                                    Toast.makeText(context, "Error: Storage permission is not granted", Toast.LENGTH_LONG).show()
                                    return@launch
                                }
                            }

                            Toast.makeText(context, "Download started.\nCheck notification for download progress.", Toast.LENGTH_SHORT).show()

                            val downloadLocation = File(Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Mejiboard/")
                            if (!downloadLocation.isDirectory)
                                downloadLocation.mkdir()

                            val instance = mainViewModel.newDownloadInstance(context, post.id, originalUrl, downloadLocation)

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
                                val tempDir = File(context.cacheDir.absolutePath + "/temp/")
                                if (!tempDir.isDirectory)
                                    tempDir.mkdir()

                                val instance = mainViewModel.newDownloadInstance(context, post.id, url, tempDir)

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
                                        startActivity(context, Intent.createChooser(shareIntent, "Send to"), null)
                                    }
                                }

//                                Toast.makeText(context, "Feature not yet available", Toast.LENGTH_SHORT).show()
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

                            val instance = mainViewModel.newDownloadInstance(context, post.id, originalUrl, tempDir)

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
                                    startActivity(context, Intent.createChooser(shareIntent, "Send to"), null)
                                }
                            }

//                            Toast.makeText(context, "Feature not yet available", Toast.LENGTH_SHORT).show()
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
                        val df = DateFormat.getDateTimeInstance()
                        SheetInfoItem(
                            "Date posted",
                            df.format(post.created_at)
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
                        val source = post.source.replace("&amp;", "&")
                        val sourceLink =
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
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceLink))
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
                                    top = 8.dp
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