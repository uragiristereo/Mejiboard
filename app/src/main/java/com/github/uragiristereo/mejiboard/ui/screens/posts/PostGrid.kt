package com.github.uragiristereo.mejiboard.ui.screens.posts

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.github.uragiristereo.mejiboard.model.network.Post
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.PostsViewModel
import com.google.accompanist.insets.navigationBarsHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalCoilApi
@Composable
fun PostsGrid(
    postsViewModel: PostsViewModel,
    mainViewModel: MainViewModel,
    mainNavigation: NavHostController,
    gridState: LazyListState,
    toolbarHeight: Dp
) {
    val context = LocalContext.current
    val gridCount = 2

    LaunchedEffect(postsViewModel.newSearch) {
        if (postsViewModel.newSearch) {
            gridState.scrollToItem(0)
            postsViewModel.newSearch = false
        }
    }

    LaunchedEffect(true) {
        launch {
            while (true) {
                if (postsViewModel.page.inc().times(100) - gridState.firstVisibleItemIndex <= gridCount.times(gridCount.inc()).inc())
                    postsViewModel.getPosts(mainViewModel.searchTags, false, mainViewModel.safeListingOnly)

                delay(200)
            }
        }
    }

    LazyColumn(
        state = gridState,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 0.dp, top = toolbarHeight)
    ) {
        val supportedTypesAnimation = listOf("gif", "webm", "mp4")

        item {
            Text(
                if (mainViewModel.searchTags == "") "Browse: All posts" else "Browse: ${mainViewModel.searchTags}",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.subtitle1
            )
        }
        itemsIndexed(postsViewModel.postsData) { index, _ ->
            if (index % gridCount == 0) {
                Row(
                    Modifier.padding(top = if (index != 0) 8.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val items: ArrayList<Post> = arrayListOf()

                    repeat(gridCount) {
                        val itemIndex = index + it
                        if (postsViewModel.postsData.size > itemIndex) items.add(postsViewModel.postsData[itemIndex])
                    }

                    items.forEachIndexed { index, item ->
                        val imageType = File(item.image).extension

//                        val url =
//                            "https://img3.gelbooru.com/thumbnails/" + item.directory + "/thumbnail_" + item.image.replace(imageType, "jpg")

                        val url =
                            "https://img3.gelbooru.com/thumbnails/" + item.directory + "/thumbnail_" + item.hash + ".jpg"

                        val borderColor = when(imageType) {
                            "gif" -> Color.Cyan
                            "webm" -> Color.Blue
                            "mp4" -> Color.Blue
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .padding(
                                    start = if (index > 0) 4.dp else 0.dp,
                                    end = if (index == 0) 4.dp else 0.dp
                                )
                                .weight(1f)
                                .aspectRatio(3f / 4f)
                                .border(
                                    BorderStroke(
                                        if (imageType in supportedTypesAnimation) 4.dp else 0.dp,
                                        borderColor
                                    )
                                )
                                .clickable(onClick = {
                                    val selectedPost = postsViewModel.postsData.find { it.id == item.id }!!
                                    mainViewModel.saveSelectedPost(selectedPost)

                                    mainNavigation.navigate("image") {
                                        popUpTo("main") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                })
                        ) {
                            CompositionLocalProvider(LocalImageLoader provides mainViewModel.imageLoader) {
                                val placeholder = remember { GradientDrawable() }
                                placeholder.setSize(item.width, item.height)
                                placeholder.setColor(android.graphics.Color.DKGRAY)

                                Image(
                                    painter = rememberImagePainter(
                                        ImageRequest.Builder(context)
                                            .data(url)
                                            .placeholder(placeholder)
                                            .crossfade(170)
                                            .build()
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        if (items.size == 1) {
                            Surface(
                                Modifier
                                    .padding(
                                        start = 4.dp
                                    )
                                    .weight(1f)
                                    .aspectRatio(3f / 4f),
                                color = MaterialTheme.colors.background
                            ) {}
                        }
                    }
                }
            }
        }
        if (postsViewModel.postsProgressVisible) {
            item {
                Box(
                    if (postsViewModel.postsData.isEmpty())
                        Modifier
                            .fillMaxWidth()
                            .fillParentMaxHeight(0.9f)
                    else
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        item {
            Spacer(Modifier.navigationBarsHeight(56.dp))
        }
    }
}