package com.github.uragiristereo.mejiboard.ui.screens.posts

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.ui.components.DrawerItem
import com.github.uragiristereo.mejiboard.ui.components.ThumbPill
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.PostsViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.ui.TopAppBar
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
    val postsData by postsViewModel.postsData
    val gridState = rememberLazyListState()

    var toolbarOffsetHeightPx by remember { mutableStateOf(0f) }

    if (mainViewModel.refreshNeeded) {
        postsViewModel.getPosts(mainViewModel.searchTags, true, mainViewModel.safeListingOnly)
        mainViewModel.refreshNeeded = false
    }

    BackHandler(
        enabled = drawerState.isOpen
    ) {
        scope.launch {
            drawerState.close()
        }
    }

    BackHandler(
        enabled = gridState.firstVisibleItemIndex >= 1
    ) {
        scope.launch {
            gridState.animateScrollToItem(0)
        }
        toolbarOffsetHeightPx = 0f
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
                        mainNavigation.navigate("settings") {
                            popUpTo("main") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = Icons.Outlined.Settings,
                    text = "Settings",
                    selected = false,
                    darkTheme = mainViewModel.isDesiredThemeDark
                )
                DrawerItem(
                    onClick = {
                        mainNavigation.navigate("about") {
                            popUpTo("main") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch {
                            drawerState.close()
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
            floatingActionButton = {
                MaterialFade(
                    visible = postsViewModel.fabVisible,
                    exitDurationMillis = MotionConstants.motionDurationShort2
                ) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                gridState.animateScrollToItem(0)
                            }
                            toolbarOffsetHeightPx = 0f
                        }
                    ) {
                        Icon(Icons.Outlined.KeyboardArrowUp, "Scroll to top")
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    Modifier
                        .navigationBarsPadding(),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                        Row {
                            Surface(
                                Modifier
                                    .width(128.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(50),
//                                color = if (mainViewModel.isDesiredThemeDark) MaterialTheme.colors.surface else MaterialTheme.colors.primary
                                color = MaterialTheme.colors.surface
                            ) {
                                Row(
                                    Modifier
                                        .clickable(onClick = {
                                            mainNavigation.navigate("search") {
                                                popUpTo("main")
                                                this.restoreState = true
                                            }
                                        }),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Icon(Icons.Default.Search, "Search")
                                    Text("SEARCH")
                                }
                            }
                            IconButton(
                                onClick = {
                                    dropDownExpanded.value = true
                                }) {
                                Icon(Icons.Default.MoreVert, "More")
                            }
                            DropdownMenu(
                                expanded = dropDownExpanded.value,
                                offset = DpOffset(52.dp, 0.dp),
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
        ) { innerPadding ->
            val toolbarHeight = 56.dp
            val toolbarHeightPx = with (LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                        val delta = available.y
                        val newOffset = toolbarOffsetHeightPx + delta
                        toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                        return Offset.Zero
                    }
                }
            }

            val modifier = Modifier
                .statusBarsPadding()
                .padding(innerPadding)
            Box(
                if (!postsViewModel.postsProgressVisible)
                    modifier.nestedScroll(nestedScrollConnection)
                else
                    modifier
            ) {
                if (postsViewModel.postsError.isEmpty()) {
                    PostsGrid(postsData, postsViewModel, mainViewModel, mainNavigation, gridState, toolbarHeight)
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
                Column(
                    Modifier
                        .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt()) }
                ) {
                    TopAppBar(
                        backgroundColor = MaterialTheme.colors.background,
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
                }
            }
        }
    }
}
