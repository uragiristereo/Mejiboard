package com.github.uragiristereo.mejiboard.presentation.posts

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedStatusBarsPadding
import com.github.uragiristereo.mejiboard.presentation.main.LocalFixedInsets
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun PostsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: PostsViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val navigationBarsPadding = LocalFixedInsets.current.navigationBarsPadding

    val drawerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()

    val preferences = mainViewModel.preferences

    val isLight = MaterialTheme.colors.isLight
    val surfaceColor = MaterialTheme.colors.surface

    val toolbarHeight = remember { 56.dp }
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }

    val fabVisible by remember {
        derivedStateOf {
            viewModel.state.toolbarOffsetHeightPx == 0f && gridState.firstVisibleItemIndex >= 5
        }
    }
    val gridCount by remember {
        derivedStateOf {
            when (configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 5
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
            viewModel.updateState { it.copy(toolbarOffsetHeightPx = 0f) }

            if (viewModel.savedState.loadFromSession) {
                viewModel.getPostsFromSession()
            } else {
                viewModel.getPosts(mainViewModel.searchTags, true, preferences.safeListingOnly)
            }

            mainViewModel.refreshNeeded = false
        }
    }

    LaunchedEffect(key1 = viewModel.state.loading) {
        if (!viewModel.state.loading) {
            viewModel.updateSessionPosts()

            if (viewModel.state.jumpToPosition) {
                viewModel.updateState { it.copy(jumpToPosition = false) }

                scope.launch {
                    delay(timeMillis = 50L)

                    gridState.scrollToItem(
                        viewModel.savedState.scrollIndex,
                        viewModel.savedState.scrollOffset
                    )
                }
            }
        }
    }

// TODO: fix scrolling lag

//    val animatedToolbarOffsetHeightPx by animateFloatAsState(
//        targetValue = offsetToAnimate,
//        finishedListener = {
//            animationInProgress = false
//        },
//    )
//    DisposableEffect(key1 = gridState.isScrollInProgress) {
//        scope.launch {
//            if (!gridState.isScrollInProgress) {
//                if (gridState.firstVisibleItemIndex > 0 && toolbarOffsetHeightPx != -toolbarHeightPx + -browseHeightPx && toolbarOffsetHeightPx != 0f) {
//                    delay(timeMillis = 50L)
//
//                    animationInProgress = true
//
//                    val half = (toolbarHeightPx + browseHeightPx) / 2
//
//                    val oldToolbarOffsetHeightPx = toolbarOffsetHeightPx
//
//                    toolbarOffsetHeightPx = when {
//                        -toolbarOffsetHeightPx >= half -> -toolbarHeightPx + -browseHeightPx
//                        else -> 0f
//                    }
//
//                    gridState.animateScrollBy(value = oldToolbarOffsetHeightPx - toolbarOffsetHeightPx)
//                }
//            }
//        }
//
//        onDispose { }
//    }

    LaunchedEffect(
        key1 = isMoreLoadingVisible,
        key2 = viewModel.state.posts.size,
    ) {
        if (isMoreLoadingVisible && viewModel.state.posts.size == (viewModel.state.page + 1) * 100) {
            viewModel.getPosts(
                searchTags = mainViewModel.searchTags,
                refresh = false,
                safeListingOnly = preferences.safeListingOnly,
            )
        }
    }

    LaunchedEffect(viewModel.state.newSearch) {
        if (viewModel.state.newSearch) {
            launch {
                delay(timeMillis = 200L)

                gridState.scrollToItem(index = 0)
                viewModel.updateState { it.copy(newSearch = false) }
            }
        }
    }

    BackHandler(enabled = drawerState.isVisible && viewModel.state.confirmExit) {
        scope.launch { drawerState.hide() }
    }

    BackHandler(enabled = viewModel.state.confirmExit && !drawerState.isVisible) {
        scope.launch {
            viewModel.updateState { it.copy(confirmExit = false) }
            scaffoldState.snackbarHostState.showSnackbar(
                "Press BACK again to exit Mejiboard",
                null,
                SnackbarDuration.Short
            )
            viewModel.updateState { it.copy(confirmExit = true) }
        }
    }

    if (mainViewModel.updateDialogVisible && mainViewModel.remindLaterCounter == -1) {
        UpdateDialog(mainViewModel = mainViewModel)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            PostsFab(
                visible = fabVisible,
                onClick = {
                    scope.launch {
                        gridState.animateScrollToItem(index = 0)
                    }
                }
            )
        },
        bottomBar = {
            PostsBottomAppBar(
                drawerState = drawerState,
                moreDropDownExpanded = viewModel.state.moreDropDownExpanded,
                onNavigate = { route ->
                    mainNavigation.navigate(route)
                },
                onDropDownExpandedChange = { value ->
                    viewModel.updateState { it.copy(moreDropDownExpanded = value) }
                },
                onDropDownClicked = { item ->
                    if (item == "all_post") {
                        mainViewModel.saveSearchTags(query = "")
                    }

                    viewModel.updateState {
                        it.copy(
                            toolbarOffsetHeightPx = 0f,
                            moreDropDownExpanded = false,
                        )
                    }
                    viewModel.getPosts(
                        searchTags = mainViewModel.searchTags,
                        refresh = true,
                        safeListingOnly = preferences.safeListingOnly,
                    )
                },
            )
        },
        modifier = Modifier.padding(
            start = navigationBarsPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = navigationBarsPadding.calculateEndPadding(LocalLayoutDirection.current),
        ),
    ) {
        Box(
            modifier = Modifier
                .fixedStatusBarsPadding()
                .nestedScroll(
                    connection = remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(
                                available: Offset,
                                source: NestedScrollSource
                            ): Offset {
                                if (!viewModel.state.loading && viewModel.state.posts.size > 4) {
                                    viewModel.scrollJob?.cancel()

                                    viewModel.scrollJob = scope.launch {
                                        val delta = available.y
                                        val newOffset =
                                            viewModel.state.toolbarOffsetHeightPx + delta

                                        viewModel.updateState {
                                            it.copy(
                                                toolbarOffsetHeightPx = newOffset.coerceIn(
                                                    minimumValue = -toolbarHeightPx + -viewModel.state.browseHeightPx,
                                                    maximumValue = 0f,
                                                )
                                            )
                                        }
                                    }
                                }

                                return Offset.Zero
                            }
                        }
                    }
                ),
        ) {
            if (viewModel.state.error.isEmpty())
                PostsGrid(
                    posts = viewModel.state.posts,
                    gridState = gridState,
                    gridCount = gridCount,
                    loading = viewModel.state.loading,
                    page = viewModel.state.page,
                    toolbarHeight = toolbarHeight,
                    browseHeightPx = viewModel.state.browseHeightPx,
                    allowPostClick = viewModel.state.allowPostClick,
                    onNavigateImage = { item ->
                        viewModel.updateState { it.copy(allowPostClick = false) }
                        mainViewModel.saveSelectedPost(item)
                        mainViewModel.backPressedByGesture = false

                        mainNavigation.navigate(
                            route = "${MainRoute.Image}",
                            data = MainRoute.Image.Key to item,
                        )
                    },
                )
            else
                PostsError(errorData = viewModel.state.error)

            PostsTopAppBar(
                toolbarOffsetHeightPx = viewModel.state.toolbarOffsetHeightPx,
                onBrowseHeightChange = { height ->
                    viewModel.updateState { it.copy(browseHeightPx = height) }
                },
                searchTags = mainViewModel.searchTags,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalFixedInsets.current.statusBarHeight)
                .background(
                    color = when {
                        MaterialTheme.colors.isLight -> Color.White
                        else -> Color.Black
                    }
                ),
        )
    }

    PostsBottomDrawer(
        mainNavigation = mainNavigation,
        drawerState = drawerState,
    )
}