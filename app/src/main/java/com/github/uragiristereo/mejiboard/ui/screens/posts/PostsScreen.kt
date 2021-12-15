package com.github.uragiristereo.mejiboard.ui.screens.posts

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.ui.components.DrawerItem
import com.github.uragiristereo.mejiboard.ui.components.ThumbPill
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.PostsViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import soup.compose.material.motion.MaterialFade
import soup.compose.material.motion.MotionConstants
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun MainScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    postsViewModel: PostsViewModel = hiltViewModel()
) {
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val dropDownExpanded = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val drawerItemSelected = remember { mutableStateOf("home") }
    val gridState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()

    var toolbarOffsetHeightPx by remember { mutableStateOf(0f) }
    var confirmExit by remember { mutableStateOf(true) }
    val activity = (LocalContext.current as? Activity)
    val isLight = MaterialTheme.colors.isLight
    var fabVisible by remember { mutableStateOf(false) }

    LaunchedEffect(mainViewModel.refreshNeeded) {
        if (mainViewModel.refreshNeeded) {
            postsViewModel.getPosts(mainViewModel.searchTags, true, mainViewModel.safeListingOnly)
            mainViewModel.refreshNeeded = false
        }

    }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == BottomDrawerValue.Closed)
            systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = isLight, navigationBarContrastEnforced = false)
    }

    LaunchedEffect(true) {
        launch {
            while (true) {
                fabVisible = (toolbarOffsetHeightPx.toInt() == 0 && gridState.firstVisibleItemIndex >= 5)

                delay(100)
            }
        }
    }

    BackHandler(
        enabled = drawerState.isOpen && confirmExit
    ) {
        scope.launch {
            drawerState.close()
        }
    }

    BackHandler(
        enabled = confirmExit && drawerState.isClosed
    ) {
        scope.launch {
            confirmExit = false
            scaffoldState.snackbarHostState.showSnackbar("Press BACK again to exit Mejiboard", null, SnackbarDuration.Short)
            confirmExit = true
        }
    }

    BackHandler(
        enabled = !confirmExit && drawerState.isClosed
    ) {
        activity?.finish()
    }

    BottomDrawer(
        drawerState = drawerState,
        drawerShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        drawerContent = {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ThumbPill()
                }
//                DrawerHeader()
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            bottom = 16.dp
                        )
                ) {
                    Text(
                        "Mejiboard",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        BuildConfig.VERSION_NAME,
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onSurface.copy(0.7f)
                    )
                }
                Divider(
                    Modifier
                        .padding(bottom = 8.dp)
                )
                DrawerItem(
                    onClick = {
                        drawerItemSelected.value = "home"
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = if (drawerItemSelected.value == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    text = "Home",
                    selected = drawerItemSelected.value == "home",
                    darkTheme = mainViewModel.isDesiredThemeDark
                )
                DrawerItem(
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            mainNavigation.navigate("settings") {
                                popUpTo("main") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = Icons.Outlined.Settings,
                    text = "Settings",
                    selected = false,
                    darkTheme = mainViewModel.isDesiredThemeDark
                )
                DrawerItem(
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            mainNavigation.navigate("about") {
                                popUpTo("main") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = Icons.Outlined.Info,
                    text = "About",
                    selected = false,
                    darkTheme = mainViewModel.isDesiredThemeDark
                )
                Box(Modifier.navigationBarsPadding())
            }
        },
        gesturesEnabled = drawerState.isOpen,
        scrimColor = Color(0xFF121212).copy(alpha = DrawerDefaults.ScrimOpacity)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                MaterialFade(
                    visible = fabVisible,
                    exitDurationMillis = MotionConstants.motionDurationShort2
                ) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                gridState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.KeyboardArrowUp, "Scroll to top")
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
                    contentPadding = rememberInsetsPaddingValues(
                        LocalWindowInsets.current.navigationBars,
                        applyTop = false,
                    ),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val navigationBarColor = MaterialTheme.colors.surface.copy(alpha = 0.4f)
                        IconButton(
                            onClick = {
                                systemUiController.setNavigationBarColor(navigationBarColor)
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                        Row(
                            Modifier
                                .weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                Modifier
                                    .width(128.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(50),
//                                color = if (mainViewModel.isDesiredThemeDark) MaterialTheme.colors.surface else MaterialTheme.colors.primary
                                color = Color.Transparent
                            ) {
                                Row(
                                    Modifier
                                        .clickable(onClick = {
//                                            mainNavigation.popBackStack()
//                                            mainNavigation.navigate("search")
                                            mainNavigation.navigate("search") {
                                                popUpTo("main") { saveState = true }
//                                                launchSingleTop = true
//                                                restoreState = true
                                            }
                                        }),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Icon(Icons.Default.Search, "Search")
                                    Text("SEARCH")
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                dropDownExpanded.value = true
                            }) {
                            Icon(Icons.Default.MoreVert, "More")
                            DropdownMenu(
                                expanded = dropDownExpanded.value,
                                onDismissRequest = { dropDownExpanded.value = false }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        dropDownExpanded.value = false
                                        postsViewModel.getPosts(mainViewModel.searchTags, true, mainViewModel.safeListingOnly)
                                        toolbarOffsetHeightPx = 0f
                                    }
                                ) {
                                    Text("Refresh")
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        dropDownExpanded.value = false
                                        mainViewModel.searchTags = ""
                                        postsViewModel.getPosts(mainViewModel.searchTags, true, mainViewModel.safeListingOnly)
                                        toolbarOffsetHeightPx = 0f
                                    }
                                ) {
                                    Text("All posts")
                                }
                            }
                        }
                    }
                }
            }
        ) {
            val toolbarHeight = 56.dp
            val toolbarHeightPx = with (LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
            var browseHeightPx by remember { mutableStateOf(0) }

//            val smoothToolbarOffsetHeightPx by animateFloatAsState(
//                targetValue = toolbarOffsetHeightPx,
//                animationSpec = tween(durationMillis = 200),
//            )

//            LaunchedEffect(gridState.isScrollInProgress) {
//                delay(400)
//                if (!gridState.isScrollInProgress && toolbarOffsetHeightPx != -toolbarHeightPx && toolbarOffsetHeightPx != 0f) {
//                    val half = -toolbarHeightPx / 2
//
//                    toolbarOffsetHeightPx =
//                        if (toolbarOffsetHeightPx >= half)
//                            0f
//                        else
//                            -toolbarHeightPx
//                }
//            }

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

            val modifier = Modifier
                .statusBarsPadding()
            Box(
                if (!postsViewModel.postsProgressVisible && postsViewModel.postsData.size > 4)
                    modifier.nestedScroll(nestedScrollConnection)
                else
                    modifier
            ) {
                if (postsViewModel.postsError.isEmpty()) {
                    PostsGrid(postsViewModel, mainViewModel, mainNavigation, gridState, toolbarHeight, browseHeightPx)
                } else {
                    Column(
                        Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            "Error",
                            Modifier.padding(16.dp)
                        )
                        Text(
                            "Error:\n(${postsViewModel.postsError})",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Card(
                    Modifier
                        .alpha(if (toolbarOffsetHeightPx.roundToInt() == -toolbarHeightPx.roundToInt() + -browseHeightPx) 0f else 1f)
                        .offset {
                            IntOffset(
                                x = 0,
                                y =
                                    toolbarOffsetHeightPx.roundToInt()
//                                    if (gridState.isScrollInProgress)
//                                        toolbarOffsetHeightPx.roundToInt()
//                                    else
//                                        smoothToolbarOffsetHeightPx.roundToInt()
                                )
                    },
                    elevation = 4.dp,
                    shape = RectangleShape
                ) {
                    Column {
                        TopAppBar(
                            backgroundColor = Color.Transparent,
                            elevation = 0.dp,
                            modifier = Modifier
                                .height(toolbarHeight),
                            title = { Text("Mejiboard") },
                            navigationIcon = {
                                Surface(
                                    Modifier
                                        .padding(8.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.not_like_tsugu),
                                        contentDescription = "App icon"
                                    )
                                }
                            }
                        )
                        Text(
                            buildAnnotatedString {
                                append("Browse: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    if (mainViewModel.searchTags.isEmpty())
                                        append("All posts")
                                    else
                                        append(mainViewModel.searchTags)
                                }
                            },
                            modifier = Modifier
                                .onGloballyPositioned { browseHeightPx = it.size.height }
                                .background(MaterialTheme.colors.background)
                                .fillMaxWidth()
                                .padding(
                                    top = 4.dp,
                                    bottom = 8.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
