package com.github.uragiristereo.mejiboard.presentation.posts

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.posts.appbar.PostsBottomAppBar
import com.github.uragiristereo.mejiboard.presentation.posts.appbar.PostsTopAppBar
import com.github.uragiristereo.mejiboard.presentation.posts.common.PostsError
import com.github.uragiristereo.mejiboard.presentation.posts.common.PostsFab
import com.github.uragiristereo.mejiboard.presentation.posts.common.UpdateDialog
import com.github.uragiristereo.mejiboard.presentation.posts.drawer.PostsBottomDrawer
import com.github.uragiristereo.mejiboard.presentation.posts.grid.PostsGrid
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun PostsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    postsViewModel: PostsViewModel = hiltViewModel()
) {
    val drawerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()
    val isLight = MaterialTheme.colors.isLight
    val surfaceColor = MaterialTheme.colors.surface
    val preferences = mainViewModel.preferences

    val toolbarHeight = remember { 56.dp }
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
    var toolbarOffsetHeightPx by postsViewModel.toolbarOffsetHeightPx
    var browseHeightPx by remember { mutableStateOf(0f) }
    var dropDownExpanded by remember { mutableStateOf(false) }
    var confirmExit by remember { mutableStateOf(true) }
    var fabVisible by remember { mutableStateOf(false) }
    val gridCount = remember { 2 }
    var animationInProgress by remember { mutableStateOf(false) }
    var animDuration by remember { mutableStateOf(200) }

    val animatedToolbarOffsetHeightPx by animateFloatAsState(
        targetValue = toolbarOffsetHeightPx,
        animationSpec = tween(durationMillis = animDuration),
    )

    DisposableEffect(key1 = postsViewModel) {
        postsViewModel.allowPostClick = true
        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
            systemUiController.setStatusBarColor(Color.Black)
            systemUiController.setNavigationBarColor(surfaceColor)
        } else {
            if (mainViewModel.isDesiredThemeDark)
                systemUiController.setStatusBarColor(Color.Black)
            else
                systemUiController.setStatusBarColor(surfaceColor)

            if (drawerState.currentValue == ModalBottomSheetValue.Hidden)
                systemUiController.setNavigationBarColor(
                    color = Color.Transparent,
                    darkIcons = isLight,
                    navigationBarContrastEnforced = false,
                )
        }

        onDispose { }
    }

    LaunchedEffect(key1 = mainViewModel.refreshNeeded) {
        if (mainViewModel.refreshNeeded) {
            toolbarOffsetHeightPx = 0f

            if (postsViewModel.loadFromSession) {
                postsViewModel.getPostsFromSession()
            } else {
                postsViewModel.getPosts(mainViewModel.searchTags, true, preferences.safeListingOnly)
            }

            mainViewModel.refreshNeeded = false
        }
    }

    DisposableEffect(key1 = Unit) {
        val job = scope.launch {
            while (true) {
                postsViewModel.updateSessionPosition(
                    index = gridState.firstVisibleItemIndex,
                    offset = gridState.firstVisibleItemScrollOffset,
                )

                delay(1000)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    DisposableEffect(key1 = postsViewModel.postsProgressVisible) {
        if (!postsViewModel.postsProgressVisible) {
            postsViewModel.updateSessionPosts()

            if (postsViewModel.jumpToPosition) {
                postsViewModel.jumpToPosition = false

                scope.launch {
                    gridState.scrollToItem(postsViewModel.sessionIndex, postsViewModel.sessionOffset)
                }
            }
        }

        onDispose { }
    }

    DisposableEffect(key1 = Unit) {
        val job = scope.launch(Dispatchers.Main.immediate) {
            while (true) {
                if (postsViewModel.page.inc().times(100) - gridState.firstVisibleItemIndex <= gridCount.times(gridCount.inc()).inc())
                    postsViewModel.getPosts(mainViewModel.searchTags, false, preferences.safeListingOnly)

                fabVisible = toolbarOffsetHeightPx == 0f && gridState.firstVisibleItemIndex >= 5

                delay(350)
            }
        }

        onDispose {
            job.cancel()
        }
    }

    BackHandler(enabled = drawerState.isVisible && confirmExit) {
        scope.launch { drawerState.hide() }
    }

    BackHandler(enabled = confirmExit && !drawerState.isVisible) {
        scope.launch {
            confirmExit = false
            scaffoldState.snackbarHostState.showSnackbar("Press BACK again to exit Mejiboard", null, SnackbarDuration.Short)
            confirmExit = true
        }
    }

    DisposableEffect(key1 = gridState.isScrollInProgress) {
        scope.launch {
            if (!gridState.isScrollInProgress) {
                delay(50)

                if (gridState.firstVisibleItemIndex > 0 && toolbarOffsetHeightPx != -toolbarHeightPx + -browseHeightPx && toolbarOffsetHeightPx != 0f) {
                    animationInProgress = true

                    val half = toolbarHeightPx / 2

                    if (-toolbarOffsetHeightPx >= half) {
                        animDuration = 250
                        toolbarOffsetHeightPx = -toolbarHeightPx + -browseHeightPx
                    } else {
                        animDuration = 150
                        toolbarOffsetHeightPx = 0f
                    }

                    delay(animDuration.toLong())

                    animationInProgress = false
                }
            } else {
                animationInProgress = false
            }
        }

        onDispose { }
    }

    if (mainViewModel.updateDialogVisible && mainViewModel.remindLaterCounter == -1) {
        UpdateDialog(mainViewModel = mainViewModel)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            PostsFab(
                visible = fabVisible,
                gridState = gridState,
            )
        },
        bottomBar = {
            PostsBottomAppBar(
                mainNavigation = mainNavigation,
                drawerState = drawerState,
                dropDownExpanded = dropDownExpanded,
                onDropDownExpandedChange = { dropDownExpanded = it },
                onToolbarOffsetHeightPxChange = { toolbarOffsetHeightPx = it },
                mainViewModel = mainViewModel,
            )
        }
    ) {
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = toolbarOffsetHeightPx + delta

                    toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx + -browseHeightPx, 0f)

                    return Offset.Zero
                }
            }
        }

        Box(
            modifier = Modifier
                .statusBarsPadding()
                .let {
                    if (!postsViewModel.postsProgressVisible && postsViewModel.postsData.size > 4)
                        it.nestedScroll(connection = nestedScrollConnection)
                    else
                        it
                },
        ) {
            if (postsViewModel.postsError.isEmpty())
                PostsGrid(
                    mainViewModel = mainViewModel,
                    mainNavigation = mainNavigation,
                    gridState = gridState,
                    toolbarHeight = toolbarHeight,
                    browseHeightPx = browseHeightPx,
                )
            else
                PostsError(errorData = postsViewModel.postsError)

            PostsTopAppBar(
                toolbarOffsetHeightPx = toolbarOffsetHeightPx,
                animatedToolbarOffsetHeightPx = animatedToolbarOffsetHeightPx,
                animationInProgress = animationInProgress,
                onBrowseHeightChange = { browseHeightPx = it },
                searchTags = mainViewModel.searchTags,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(insets = WindowInsets.statusBars)
                .background(color = MaterialTheme.colors.background),
        )
    }

    PostsBottomDrawer(
        mainNavigation = mainNavigation,
        drawerState = drawerState,
        mainViewModel = mainViewModel,
    )
}