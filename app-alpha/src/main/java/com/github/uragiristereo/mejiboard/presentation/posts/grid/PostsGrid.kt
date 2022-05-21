package com.github.uragiristereo.mejiboard.presentation.posts.grid

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.main.LocalFixedInsets
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostItem
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostPlaceholder
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostsProgress

@ExperimentalCoilApi
@Composable
fun PostsGrid(
    posts: List<Post>,
    unfilteredPostsCount: Int,
    gridState: LazyListState,
    gridCount: Int,
    loading: Boolean,
    page: Int,
    toolbarHeight: Dp,
    browseHeightPx: Float,
    allowPostClick: Boolean,
    onNavigateImage: (Post) -> Unit,
) {
    val density = LocalDensity.current
    val navigationBarsPadding = LocalFixedInsets.current.navigationBarsPadding

    Crossfade(targetState = loading && page == 0) { target ->
        if (!target) {
            LazyColumn(
                state = gridState,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = navigationBarsPadding.calculateTopPadding() + toolbarHeight + with(density) { browseHeightPx.toDp() },
                    bottom = navigationBarsPadding.calculateBottomPadding() + 56.dp + 8.dp,
                ),
            ) {
                itemsIndexed(
                    items = posts,
                    key = { _, item ->
                        item.id
                    },
                ) { index, _ ->
                    if (index % gridCount == 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = if (index != 0) 8.dp else 0.dp),
                        ) {
                            val items = mutableListOf<Post>()

                            repeat(gridCount) {
                                val itemIndex = index + it
                                if (posts.size > itemIndex) items.add(posts[itemIndex])
                            }

                            items.forEachIndexed { index, item ->
                                PostItem(
                                    item = item,
                                    allowPostClick = allowPostClick,
                                    onNavigateImage = onNavigateImage,
                                    modifier = Modifier.weight(weight = 1f),
                                )

                                if (index != gridCount - 1) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }

                            repeat(times = gridCount - items.size) {
                                PostPlaceholder(modifier = Modifier.weight(weight = 1f))
                            }
                        }
                    }
                }

                if (posts.isNotEmpty() && (unfilteredPostsCount == (page + 1) * 100 || loading)) {
                    item(key = Constants.KEY_LOAD_MORE_PROGRESS) {
                        PostsProgress()
                    }
                }
            }
        } else {
            PostsProgress(modifier = Modifier.fillMaxHeight())
        }
    }
}