package com.github.uragiristereo.mejiboard.presentation.posts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.common.extension.navigate
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute
import com.github.uragiristereo.mejiboard.presentation.posts.appbar.PostsBottomAppBar
import com.github.uragiristereo.mejiboard.presentation.posts.appbar.PostsTopAppBar
import com.github.uragiristereo.mejiboard.presentation.posts.common.PostsError
import com.github.uragiristereo.mejiboard.presentation.posts.common.PostsFab
import com.github.uragiristereo.mejiboard.presentation.posts.common.UpdateDialog
import com.github.uragiristereo.mejiboard.presentation.posts.drawer.PostsBottomDrawer
import com.github.uragiristereo.mejiboard.presentation.posts.grid.PostsGrid
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun PostsScreen(
    tags: String,
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: PostsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current

    val drawerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyStaggeredGridState()
    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()

    val isLight = MaterialTheme.colors.isLight
    val surfaceColor = MaterialTheme.colors.surface
    val navigationBarColor = MaterialTheme.colors.surface.copy(alpha = 0.4f)

    var topAppBarHeight by remember { mutableStateOf(0.dp) }

    remember {
        viewModel.updateState { it.copy(tags = tags) }

        true
    }

    remember(mainViewModel.state.selectedProvider) {
        viewModel.updateState { it.copy(selectedProvider = mainViewModel.state.selectedProvider) }

        true
    }

    val gridCount by remember {
        derivedStateOf {
            when (configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 4
            }
        }
    }

    val isMoreLoadingVisible by remember {
        derivedStateOf {
            gridState.layoutInfo.visibleItemsInfo
                .filter { it.key.toString() == Constants.KEY_LOAD_MORE_PROGRESS }
                .size == 1
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        var job: Job? = null

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    job = scope.launch {
                        while (true) {
                            viewModel.updateSessionPosition(
                                index = gridState.firstVisibleItemIndex,
                                offset = gridState.firstVisibleItemScrollOffset,
                            )

                            delay(timeMillis = 1000L)
                        }
                    }
                }

                Lifecycle.Event.ON_RESUME -> {}
                else -> {
                    job?.cancel()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            job?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.updateState { it.copy(allowPostClick = true) }

        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
            systemUiController.setStatusBarColor(Color.Black)
            systemUiController.setNavigationBarColor(surfaceColor)
        } else {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = isLight,
            )

            if (drawerState.currentValue == ModalBottomSheetValue.Hidden)
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent,
                    darkIcons = isLight,
                    navigationBarContrastEnforced = false,
                )
        }
    }

    LaunchedEffect(key1 = mainViewModel.refreshNeeded) {
        if (mainViewModel.refreshNeeded) {
            viewModel.offsetY.snapTo(targetValue = 0f)

            if (viewModel.savedState.loadFromSession) {
                viewModel.getPostsFromSession()
            } else {
                viewModel.getPosts(refresh = true)
            }

            mainViewModel.refreshNeeded = false
        }
    }

    LaunchedEffect(key1 = viewModel.state.loading) {
        if (!viewModel.state.loading) {
            viewModel.updateSessionPosts()

            if (viewModel.state.jumpToPosition) {
                viewModel.updateState { it.copy(jumpToPosition = false) }

                launch {
                    delay(timeMillis = 50L)

                    gridState.scrollToItem(
                        index = viewModel.savedState.scrollIndex,
                        scrollOffset = viewModel.savedState.scrollOffset,
                    )
                }
            }
        }
    }

    val fabVisible by remember {
        derivedStateOf {
            if (gridState.firstVisibleItemIndex >= 5 && viewModel.posts.isNotEmpty()) {
                when (viewModel.offsetY.value) {
                    0f -> true
                    -with(density) { topAppBarHeight.toPx() } -> false
                    else -> viewModel.state.lastFabVisible
                }
            } else {
                false
            }
        }
    }

    LaunchedEffect(key1 = fabVisible) {
        viewModel.updateState { it.copy(lastFabVisible = fabVisible) }
    }

    DisposableEffect(key1 = gridState.isScrollInProgress) {
        // prevent coroutine scope to get cancelled
        scope.launch {
            if (!gridState.isScrollInProgress) {
                val topAppBarHeightPx = with(density) { topAppBarHeight.toPx() }

                if (viewModel.offsetY.value != -topAppBarHeightPx && viewModel.offsetY.value != 0f) {
                    val half = topAppBarHeightPx / 2
                    val oldOffsetY = viewModel.offsetY.value
                    val targetOffsetY = when {
                        abs(viewModel.offsetY.value) >= half -> -topAppBarHeightPx
                        else -> 0f
                    }

                    launch {
                        gridState.animateScrollBy(value = oldOffsetY - targetOffsetY)
                    }

                    launch {
                        viewModel.offsetY.animateTo(targetOffsetY)
                    }
                }
            }
        }

        onDispose { }
    }

    LaunchedEffect(
        key1 = isMoreLoadingVisible,
        key2 = viewModel.state.canLoadMore,
    ) {
        if (isMoreLoadingVisible && viewModel.state.canLoadMore) {
            viewModel.getPosts(refresh = false)
        }
    }

    BackHandler(enabled = drawerState.isVisible && viewModel.state.confirmExit) {
        scope.launch { drawerState.hide() }
    }

    BackHandler(enabled = viewModel.state.confirmExit && !drawerState.isVisible) {
        scope.launch {
            viewModel.updateState { it.copy(confirmExit = false) }
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Press BACK again to exit Mejiboard",
                actionLabel = null,
                duration = SnackbarDuration.Short
            )
            viewModel.updateState { it.copy(confirmExit = true) }
        }
    }

    BackHandler(enabled = !viewModel.state.confirmExit && !drawerState.isVisible) {
        (context as Activity).finishAffinity()
    }

    if (mainViewModel.updateDialogVisible && mainViewModel.remindLaterCounter == -1) {
        UpdateDialog(mainViewModel = mainViewModel)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            PostsFab(
                visible = fabVisible,
                onClick = remember {
                    {
                        scope.launch {
                            gridState.animateScrollToItem(index = 0)
                        }
                    }
                }
            )
        },
        bottomBar = {
            PostsBottomAppBar(
                tags = viewModel.state.tags,
                loading = isMoreLoadingVisible,
                moreDropDownExpanded = viewModel.state.moreDropDownExpanded,
                onNavigate = remember {
                    {
                        mainNavigation.navigate(
                            route = MainRoute.Search.parseRoute(value = viewModel.state.tags)
                        )
                    }
                },
                onDropDownExpandedChange = remember {
                    { value ->
                        viewModel.updateState { it.copy(moreDropDownExpanded = value) }
                    }
                },
                onDropDownClicked = remember {
                    { item ->
                        scope.launch {
                            viewModel.offsetY.animateTo(targetValue = 0f)
                        }

                        viewModel.updateState { it.copy(moreDropDownExpanded = false) }

                        fun getPosts() {
                            viewModel.getPosts(
                                refresh = true,
                                onLoaded = {
                                    scope.launch { gridState.scrollToItem(index = 0) }
                                },
                            )
                        }

                        when (item) {
                            "all_post" -> {
                                viewModel.updateState { it.copy(tags = "") }
                                getPosts()
                            }

                            "refresh" -> {
                                getPosts()
                            }
                        }
                    }
                },
                onMenuClicked = remember {
                    {
                        systemUiController.setNavigationBarColor(color = navigationBarColor)

                        scope.launch {
                            drawerState.show()
                        }
                    }
                },
            )
        },
        modifier = Modifier.padding(
            paddingValues = WindowInsets.navigationBars
                .only(sides = WindowInsetsSides.Start + WindowInsetsSides.End)
                .asPaddingValues(),
        ),
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .nestedScroll(
                    connection = remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(
                                available: Offset,
                                source: NestedScrollSource,
                            ): Offset {
                                if (!viewModel.state.loading && viewModel.posts.size > 4) {
                                    val delta = available.y
                                    val newOffset = viewModel.offsetY.value + delta

                                    scope.launch {
                                        viewModel.offsetY.snapTo(
                                            targetValue = newOffset.coerceIn(
                                                minimumValue = -with(density) { topAppBarHeight.toPx() },
                                                maximumValue = 0f,
                                            )
                                        )
                                    }
                                }

                                return Offset.Zero
                            }
                        }
                    }
                ),
        ) {
            if (viewModel.state.error.isEmpty()) {
                PostsGrid(
                    posts = viewModel.posts,
                    canLoadMore = viewModel.state.canLoadMore,
                    gridState = gridState,
                    gridCount = gridCount,
                    loading = viewModel.state.loading,
                    page = viewModel.state.page,
                    topAppBarHeight = topAppBarHeight,
                    allowPostClick = viewModel.state.allowPostClick,
                    onNavigateImage = remember {
                        { item ->
                            viewModel.updateState { it.copy(allowPostClick = false) }
                            mainViewModel.backPressedByGesture = false

                            mainNavigation.navigate(
                                route = "${MainRoute.Image}",
                                data = MainRoute.Image.Key to item,
                            )
                        }
                    },
                )
            } else {
                PostsError(
                    errorData = viewModel.state.error,
                    onRetryClick = {
                        viewModel.retryGetPosts()
                    },
                )
            }

            PostsTopAppBar(
                searchTags = viewModel.state.tags,
                currentHeight = topAppBarHeight,
                onHeightChanged = { topAppBarHeight = it },
                modifier = Modifier
                    .graphicsLayer {
                        translationY = viewModel.offsetY.value
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding()
                )
                .background(
                    color = when {
                        MaterialTheme.colors.isLight -> Color.White
                        else -> Color.Black
                    }
                ),
        )
    }

    PostsBottomDrawer(
        drawerState = drawerState,
        onNavigate = remember {
            { route ->
                mainNavigation.navigate(route)
            }
        },
    )
}