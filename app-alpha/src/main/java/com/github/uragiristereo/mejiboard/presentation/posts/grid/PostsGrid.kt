package com.github.uragiristereo.mejiboard.presentation.posts.grid

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.main.LocalFixedInsets
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.posts.PostsViewModel
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostItem
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostPlaceholder
import com.github.uragiristereo.mejiboard.presentation.posts.grid.common.PostsProgress

@ExperimentalCoilApi
@Composable
fun PostsGrid(
    mainViewModel: MainViewModel,
    mainNavigation: NavHostController,
    gridState: LazyListState,
    toolbarHeight: Dp,
    browseHeightPx: Float,
    postsViewModel: PostsViewModel = hiltViewModel(),
) {
    val density = LocalDensity.current
    val gridCount = remember { 2 }
    val navigationBarsPadding = LocalFixedInsets.current.navigationBarsPadding

    LaunchedEffect(postsViewModel.newSearch) {
        if (postsViewModel.newSearch) {
            gridState.scrollToItem(0)
            postsViewModel.newSearch = false
        }
    }

    LazyColumn(
        state = gridState,
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = navigationBarsPadding.calculateTopPadding() + toolbarHeight + with(density) { browseHeightPx.toDp() },
            bottom = navigationBarsPadding.calculateBottomPadding() + 56.dp + 8.dp,
        ),
    ) {
        itemsIndexed(postsViewModel.postsData) { index, _ ->
            if (index % gridCount == 0) {
                Row(
                    Modifier.padding(top = if (index != 0) 8.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val items = mutableListOf<Post>()

                    repeat(gridCount) {
                        val itemIndex = index + it
                        if (postsViewModel.postsData.size > itemIndex) items.add(postsViewModel.postsData[itemIndex])
                    }

                    items.forEachIndexed { index, item ->
                        PostItem(
                            mainNavigation = mainNavigation,
                            post = item,
                            index = index,
                            mainViewModel = mainViewModel,
                            modifier = Modifier.weight(1f)
                        )

                        if (items.size == 1) {
                            PostPlaceholder(modifier = Modifier.weight(weight = 1f))
                        }
                    }
                }
            }
        }

        if (postsViewModel.postsProgressVisible && postsViewModel.postsData.isNotEmpty()) {
            item {
                PostsProgress()
            }
        }
    }

    if (postsViewModel.postsProgressVisible && postsViewModel.postsData.isEmpty()) {
        PostsProgress(modifier = Modifier.fillMaxHeight())
    }
}