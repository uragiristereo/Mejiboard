package com.github.uragiristereo.mejiboard.presentation.more

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.common.SheetInfoItem
import com.github.uragiristereo.mejiboard.presentation.common.SheetItem
import com.github.uragiristereo.mejiboard.presentation.common.TagInfoItem
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.util.*

@ExperimentalMaterialApi
@Composable
fun MoreInfo(
    post: Post,
    moreNavigation: NavHostController,
    sheetState: ModalBottomSheetState,
    imageViewModel: ImageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imageType = remember { File(post.image).extension }

    LaunchedEffect(key1 = Unit) {
        imageViewModel.shareModalVisible = true
    }

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
                    df.format(post.createdAt)
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
                        "${post.sampleWidth}x${post.sampleHeight}\n${imageViewModel.imageSize} (jpg)"
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
                // 2 = ???
                // 6 = deprecated
                var artists = imageViewModel.infoData.value.filter { it.type == 1 }
                var characters =
                    imageViewModel.infoData.value.filter { it.type == 4 }
                var copyrights =
                    imageViewModel.infoData.value.filter { it.type == 3 }
                var metadatas =
                    imageViewModel.infoData.value.filter { it.type == 5 }
                var tags = imageViewModel.infoData.value.filter { it.type == 0 }

                artists = artists.sortedBy { it.name }
                characters = characters.sortedBy { it.name }
                copyrights = copyrights.sortedBy { it.name }
                metadatas = metadatas.sortedBy { it.name }
                tags = tags.sortedBy { it.name }

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
                            tag = item.name,
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
                            tag = item.name,
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
                            tag = item.name,
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
                            tag = item.name,
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
                            tag = item.name,
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