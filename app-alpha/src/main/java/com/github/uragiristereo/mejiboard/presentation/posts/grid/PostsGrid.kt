package com.github.uragiristereo.mejiboard.presentation.posts.grid

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostItem
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostsProgress

@ExperimentalCoilApi
@Composable
fun PostsGrid(
    posts: SnapshotStateList<Post>,
    canLoadMore: Boolean,
    gridState: LazyGridState,
    gridCount: Int,
    loading: Boolean,
    page: Int,
    combinedToolbarHeight: Dp,
    allowPostClick: Boolean,
    onNavigateImage: (Post) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()

    Crossfade(
        targetState = loading && page == 0,
        modifier = modifier,
    ) { target ->
        if (!target) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(count = gridCount),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = navigationBarsPadding.calculateTopPadding() + combinedToolbarHeight,
                    bottom = navigationBarsPadding.calculateBottomPadding() + 56.dp + 8.dp,
                ),
            ) {
                items(
                    items = posts,
                    key = { item ->
                        item.id
                    },
                ) { item ->
                    PostItem(
                        item = item,
                        allowPostClick = allowPostClick,
                        onNavigateImage = onNavigateImage,
                    )
                }

                if (posts.isNotEmpty() && (canLoadMore || loading)) {
                    item(
                        key = Constants.KEY_LOAD_MORE_PROGRESS,
                        span = { GridItemSpan(gridCount) },
                    ) {
                        PostsProgress()
                    }
                }
            }
        } else {
            PostsProgress(modifier = Modifier.fillMaxHeight())
        }
    }
}