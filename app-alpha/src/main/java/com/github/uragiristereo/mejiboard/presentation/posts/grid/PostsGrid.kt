package com.github.uragiristereo.mejiboard.presentation.posts.grid

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostItem
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostsProgress

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalCoilApi
@Composable
fun PostsGrid(
    posts: SnapshotStateList<Post>,
    canLoadMore: Boolean,
    gridState: LazyStaggeredGridState,
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
            LazyVerticalStaggeredGrid(
                state = gridState,
                columns = StaggeredGridCells.Fixed(count = gridCount),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = navigationBarsPadding.calculateTopPadding() + combinedToolbarHeight,
                    bottom = navigationBarsPadding.calculateBottomPadding() + 56.dp + 8.dp,
                ),
                modifier = Modifier.fillMaxSize(),
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
//                        span = { GridItemSpan(gridCount) },
                    ) {
                        PostsProgress(
                            modifier = Modifier
                                .fillMaxHeight()
                                .alpha(0f),
                        )
                    }
                }
            }
        } else {
            PostsProgress(modifier = Modifier.fillMaxHeight())
        }
    }
}