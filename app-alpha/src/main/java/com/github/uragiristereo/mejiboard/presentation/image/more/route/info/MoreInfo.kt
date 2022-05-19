package com.github.uragiristereo.mejiboard.presentation.image.more.route.info

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedNavigationBarsPadding
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.LocalMoreNavigation
import com.github.uragiristereo.mejiboard.presentation.image.more.route.core.SheetItem
import com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core.SheetInfoItem
import com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core.TagInfoItem
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat

@ExperimentalMaterialApi
@Composable
fun MoreInfo(
    sheetState: ModalBottomSheetState,
    viewModel: MoreViewModel,
) {
    val context = LocalContext.current
    val moreNavigation = LocalMoreNavigation.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    val state by viewModel.state
    val post = remember { state.selectedPost!! }
    val imageType = remember { File(post.image).extension }

    LaunchedEffect(
        key1 = sheetState.currentValue,
        key2 = sheetState.isAnimationRunning,
    ) {
        if (sheetState.currentValue == ModalBottomSheetValue.HalfExpanded && !sheetState.isAnimationRunning) {
            viewModel.state.update { it.copy(isShowTagsCollapsed = true) }
        }
    }

    Column {
        SheetItem(
            text = "Close",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                )
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
                    text = "Info",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            bottom = 4.dp,
                        ),
                )
            }

            item {
                SheetInfoItem(
                    leadingText = "Post ID",
                    trailingText = "${post.id}",
                )
            }

            item {
                val df = remember { DateFormat.getDateTimeInstance() }

                SheetInfoItem(
                    leadingText = "Date posted",
                    trailingText = df.format(post.createdAt),
                )
            }

            item {
                SheetInfoItem(
                    leadingText = "Uploader",
                    trailingText = post.owner,
                )
            }

            item {
                val rating = remember {
                    when (post.rating) {
                        "safe" -> "Safe"
                        "questionable" -> "Questionable"
                        "explicit" -> "Explicit"
                        else -> "Safe"
                    }
                }

                SheetInfoItem(
                    leadingText = "Rating",
                    trailingText = rating,
                )
            }

            if (post.source.isNotEmpty()) {
                val source = post.source.replace(oldValue = "&amp;", newValue = "&")
                val sourceLink =
                    when {
                        "https://" in source || "http://" in source -> source
                        else -> "https://www.google.com/search?q=${source}"
                    }

                item {
                    SheetInfoItem(
                        leadingText = "Source",
                        trailingText = source,
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceLink))

                                context.startActivity(intent)
                            },
                    )
                }
            }

            if (post.sample == 1 && imageType != "gif") {
                item {
                    SheetInfoItem(
                        leadingText = "Compressed (sample) image size",
                        trailingText = "${post.sampleWidth}x${post.sampleHeight}\n${state.imageSize} (jpg)",
                    )
                }
            }

            val originalImageSize = when {
                post.sample == 1 && imageType != "gif" -> state.originalImageSize
                else -> state.imageSize
            }

            item {
                SheetInfoItem(
                    leadingText = "Full (original) image size",
                    trailingText = "${post.width}x${post.height}\n$originalImageSize (${File(post.image).extension})",
                )
            }

            item {
                Divider(modifier = Modifier.padding(top = 8.dp))
            }

            if (state.isShowTagsCollapsed && !state.infoProgressVisible) {
                item {
                    SheetItem(
                        text = "Show tags",
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.expand_more),
                                contentDescription = "Close",
                            )
                        },
                        onClick = {
                            if (state.infoData.isEmpty()) {
                                viewModel.getTagsInfo(names = post.tags)
                            }

                            viewModel.state.update { it.copy(isShowTagsCollapsed = false) }
                        },
                    )
                }
            }

            if (state.infoProgressVisible) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (state.infoData.isNotEmpty() && !state.infoProgressVisible && !state.isShowTagsCollapsed) {
                // 2 = ???
                // 6 = deprecated

                item {
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 16.dp,
                                bottom = 4.dp,
                            ),
                    )
                }

                val artists = state.infoData
                    .filter { it.type == 1 }
                    .sortedBy { it.name }
                
                if (artists.isNotEmpty()) {
                    item {
                        Text(
                            text = "Artist".uppercase(),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp,
                                ),
                        )
                    }

                    items(artists) { item ->
                        TagInfoItem(
                            tag = item.name,
                            count = item.count,
                        )
                    }
                }

                val characters = state.infoData
                    .filter { it.type == 4 }
                    .sortedBy { it.name }
                
                if (characters.isNotEmpty()) {
                    item {
                        Text(
                            text = "Character".uppercase(),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp
                                ),
                        )
                    }

                    items(characters) { item ->
                        TagInfoItem(
                            tag = item.name,
                            count = item.count,
                        )
                    }
                }

                val copyrights = state.infoData
                    .filter { it.type == 3 }
                    .sortedBy { it.name }
                
                if (copyrights.isNotEmpty()) {
                    item {
                        Text(
                            text = "Copyright".uppercase(),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp,
                                ),
                        )
                    }

                    items(copyrights) { item ->
                        TagInfoItem(
                            tag = item.name,
                            count = item.count,
                        )
                    }
                }

                val metadata = state.infoData
                    .filter { it.type == 5 }
                    .sortedBy { it.name }
                
                if (metadata.isNotEmpty()) {
                    item {
                        Text(
                            text = "Metadata".uppercase(),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp,
                                ),
                        )
                    }

                    items(metadata) { item ->
                        TagInfoItem(
                            tag = item.name,
                            count = item.count,
                        )
                    }
                }
                
                val tags = state.infoData
                    .filter { it.type == 0 }
                    .sortedBy { it.name }

                if (tags.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tag".uppercase(),
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp,
                                ),
                        )
                    }

                    items(tags) { item ->
                        TagInfoItem(
                            tag = item.name,
                            count = item.count,
                        )
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier
                            .let {
                                when (configuration.orientation) {
                                    Configuration.ORIENTATION_PORTRAIT -> it.fixedNavigationBarsPadding()
                                    else -> it
                                }
                            },
                    )
                }
            }
        }
    }
}