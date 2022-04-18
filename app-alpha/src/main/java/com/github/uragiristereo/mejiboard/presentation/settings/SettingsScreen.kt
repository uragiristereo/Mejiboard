package com.github.uragiristereo.mejiboard.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.common.helper.FileHelper.convertSize
import com.github.uragiristereo.mejiboard.common.helper.FileHelper.getFolderSize
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.data.preferences.enums.DohProvider
import com.github.uragiristereo.mejiboard.data.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.data.preferences.enums.Theme
import com.github.uragiristereo.mejiboard.presentation.common.SettingsCategory
import com.github.uragiristereo.mejiboard.presentation.common.SettingsItem
import com.github.uragiristereo.mejiboard.presentation.common.SettingsOptions
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

const val ENABLE_SAFE_LISTING_TOGGLE = false

@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()
    val systemUiController = rememberSystemUiController()
    val preferences = mainViewModel.preferences

    val surfaceColor = MaterialTheme.colors.surface
    val isLight = MaterialTheme.colors.isLight

    val themes = remember { Theme::class.sealedSubclasses.map { it.objectInstance as Theme } }
    val previewSizes = remember { PreviewSize::class.sealedSubclasses.map { it.objectInstance as PreviewSize } }
    val dohProviders = remember { DohProvider::class.sealedSubclasses.map { it.objectInstance as DohProvider } }

    var cacheDirectorySize by remember { mutableStateOf("Loading...") }
    var cacheCleaned by remember { mutableStateOf(false) }
    var settingsHeaderSize by remember { mutableStateOf(0) }
    var bigHeader by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        launch {
            val size = getFolderSize(context.cacheDir)
            cacheDirectorySize = convertSize(size.toInt())
        }

        launch {
            while (true) {
                if (settingsHeaderSize > 0) {
                    val half = (0.6f * settingsHeaderSize).toInt()

                    bigHeader =
                        if (columnState.firstVisibleItemIndex == 0)
                            columnState.firstVisibleItemScrollOffset < half
                        else
                            false

                    if (columnState.firstVisibleItemIndex == 0 && columnState.firstVisibleItemScrollOffset != 0 && !columnState.isScrollInProgress) {
                        if (columnState.firstVisibleItemScrollOffset < half)
                            scope.launch { columnState.animateScrollToItem(0) }
                        else
                            scope.launch { columnState.animateScrollToItem(1) }
                    }
                }
                delay(100)
            }
        }
        mainViewModel.checkForUpdate()
    }

    DisposableEffect(key1 = surfaceColor, key2 = isLight) {
        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
            systemUiController.setStatusBarColor(Color.Black)
            systemUiController.setNavigationBarColor(surfaceColor)
        } else {
            systemUiController.setStatusBarColor(Color.Transparent, isLight)
            systemUiController.setNavigationBarColor(surfaceColor.copy(0.4f))
        }

        onDispose { }
    }

    val smallHeaderOpacity by animateFloatAsState(
        targetValue = if (bigHeader) 0f else 1f,
        animationSpec = tween(durationMillis = 350),
    )
    val bigHeaderOpacity by animateFloatAsState(
        targetValue = if (bigHeader) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
    )

    Scaffold(
        topBar = {
            Card(
                elevation = if (!bigHeader) 4.dp else 0.dp,
                shape = RectangleShape
            ) {
                TopAppBar(
                    backgroundColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp,
                    title = {
                        Text(
                            text = "Settings",
                            modifier = Modifier
                                .alpha(smallHeaderOpacity)
                        )
                    },
                    contentPadding = rememberInsetsPaddingValues(
                        LocalWindowInsets.current.statusBars,
                        applyBottom = false,
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                mainNavigation.navigateUp()
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                "Back"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            Spacer(
                Modifier
                    .navigationBarsHeight()
                    .fillMaxWidth()
            )
        }
    ) { innerPadding ->
        Box {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { _, _ -> },
                            onDragEnd = {
                                Timber.i("on drag end")
                            },
                            onDragCancel = {
                                Timber.i("on drag cancel")
                            }
                        )
                    },
                state = columnState,
                contentPadding = innerPadding
            ) {
                item {
                    Text(
                        text = "Settings",
                        modifier = Modifier
                            .alpha(bigHeaderOpacity)
                            .onGloballyPositioned {
                                settingsHeaderSize = it.size.height
                            }
                            .padding(
                                top = 48.dp,
                                bottom = 12.dp,
                                start = 16.dp
                            ),
                        style = MaterialTheme.typography.h4,
                        fontSize = 36.sp
                    )
                }
                item {
                    SettingsCategory(text = "Interface")
                }
                item {
                    SettingsOptions(
                        title = "Theme",
                        items = themes,
                        selectedItem = preferences.theme,
                        onItemSelected = {
                            mainViewModel.updatePreferences(
                                newData = preferences.copy(
                                    theme = it,
                                )
                            )
                        }
                    )
                }
                item {
                    AnimatedContent(targetState = mainViewModel.isDesiredThemeDark) { target ->
                        if (target) {
                            val blackThemeInteractionSource = remember { MutableInteractionSource() }
                            SettingsItem(
                                title = "Black dark theme",
                                subtitle = "Use pitch black theme instead of regular dark theme, useful for OLED screens",
                                interactionSource = blackThemeInteractionSource,
                                onClick = {
                                    mainViewModel.updatePreferences(
                                        newData = preferences.copy(
                                            blackTheme = !preferences.blackTheme,
                                        )
                                    )
                                },
                                action = {
                                    Switch(
                                        checked = preferences.blackTheme,
                                        interactionSource = blackThemeInteractionSource,
                                        onCheckedChange = {
                                            mainViewModel.updatePreferences(
                                                newData = preferences.copy(
                                                    blackTheme = !preferences.blackTheme,
                                                )
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
                item {
                    Divider()
                }
                item {
                    SettingsCategory(text = "Behavior")
                }
                item {
                    SettingsOptions(
                        title = "Preview size",
                        items = previewSizes,
                        selectedItem = preferences.previewSize,
                        onItemSelected = {
                            mainViewModel.updatePreferences(
                                newData = preferences.copy(
                                    previewSize = it,
                                )
                            )
                        }
                    )
                }

                if (ENABLE_SAFE_LISTING_TOGGLE) {
                    item {
                        val safeListingOnlyInteractionSource = remember { MutableInteractionSource() }
                        SettingsItem(
                            title = "Safe listing only mode",
                            subtitle = buildAnnotatedString {
                                append("Filter ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("questionable")
                                }
                                append(" & ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("explicit")
                                }
                                append(" rated posts\n")

                                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                    append("(Feature not yet toggleable)")
                                }
                            },
                            interactionSource = safeListingOnlyInteractionSource,
                            onClick = {
                                mainViewModel.updatePreferences(
                                    newData = preferences.copy(
                                        safeListingOnly = !preferences.safeListingOnly,
                                    )
                                )
                                mainViewModel.refreshNeeded = true
                            },
                            action = {
                                Switch(
                                    checked = preferences.safeListingOnly,
                                    interactionSource = safeListingOnlyInteractionSource,
                                    onCheckedChange = {
                                        mainViewModel.updatePreferences(
                                            newData = preferences.copy(
                                                safeListingOnly = !preferences.safeListingOnly,
                                            )
                                        )
                                        mainViewModel.refreshNeeded = true
                                    }
                                )
                            }
                        )
                    }
                }
                item {
                    Divider()
                }
                item {
                    SettingsCategory(text = "Advanced")
                }
                item {
                    val dohInteractionSource = remember { MutableInteractionSource() }
                    SettingsItem(
                        title = "DNS over HTTPS",
                        subtitle = buildAnnotatedString {
                            append("Enable if Gelbooru is blocked in your country ")

                            withStyle(
                                style = SpanStyle(fontWeight = FontWeight.Bold)
                            ) {
                                append("(recommended)")
                            }
                        },
                        onClick = {
                            mainViewModel.updatePreferences(
                                newData = preferences.copy(
                                    useDnsOverHttps = !preferences.useDnsOverHttps,
                                )
                            )
                            mainViewModel.renewNetworkInstance(preferences.useDnsOverHttps, preferences.dohProvider.name)
                            mainViewModel.refreshNeeded = true
                        },
                        interactionSource = dohInteractionSource,
                        action = {
                            Switch(
                                checked = preferences.useDnsOverHttps,
                                interactionSource = dohInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.updatePreferences(
                                        newData = preferences.copy(
                                            useDnsOverHttps = !preferences.useDnsOverHttps,
                                        )
                                    )
                                    mainViewModel.renewNetworkInstance(preferences.useDnsOverHttps, preferences.dohProvider.name)
                                    mainViewModel.refreshNeeded = true
                                }
                            )
                        }
                    )
                }
                item {
                    AnimatedContent(targetState = preferences.useDnsOverHttps) { target ->
                        if (target)
                            SettingsOptions(
                                title = "DNS over HTTPS provider",
                                items = dohProviders,
                                selectedItem = preferences.dohProvider,
                                onItemSelected = {
                                    mainViewModel.updatePreferences(
                                        newData = preferences.copy(
                                            dohProvider = it,
                                        )
                                    )
                                }
                            )
                    }
                }
                item {
                    val blockFromRecentsInteractionSource = remember { MutableInteractionSource() }
                    SettingsItem(
                        title = "Block content from Recent apps",
                        subtitle = "Prevent content for showing in Recent apps",
                        onClick = {
                            mainViewModel.updatePreferences(
                                newData = preferences.copy(
                                    blockFromRecents = !preferences.blockFromRecents,
                                )
                            )
                        },
                        interactionSource = blockFromRecentsInteractionSource,
                        action = {
                            Switch(
                                checked = preferences.blockFromRecents,
                                interactionSource = blockFromRecentsInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.updatePreferences(
                                        newData = preferences.copy(
                                            blockFromRecents = !preferences.blockFromRecents,
                                        )
                                    )
                                }
                            )
                        }
                    )
                }
                item {
                    Divider()
                }
                item {
                    SettingsCategory(text = "Miscellaneous")
                }
                item {
                    val autoCleanCacheInteractionSource = remember { MutableInteractionSource() }
                    SettingsItem(
                        title = "Auto clean cache",
                        subtitle = "Automatically clean cache that older than 12 hours at startup",
                        onClick = {
                            mainViewModel.updatePreferences(
                                newData = preferences.copy(
                                    autoCleanCache = !preferences.autoCleanCache,
                                )
                            )
                        },
                        interactionSource = autoCleanCacheInteractionSource,
                        action = {
                            Switch(
                                checked = preferences.autoCleanCache,
                                interactionSource = autoCleanCacheInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.updatePreferences(
                                        newData = preferences.copy(
                                            autoCleanCache = !preferences.autoCleanCache,
                                        )
                                    )
                                }
                            )
                        }
                    )
                }
                item {
                    SettingsItem(
                        title = "Clear cache now",
                        subtitle = buildAnnotatedString {
                            append("Remove all cached memory and files\n")

                            if (cacheCleaned)
                                append("Cache successfully cleaned!")
                            else {
                                append("Cache size: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(cacheDirectorySize)
                                }
                                append(" (estimated)")
                            }
                        },
                        onClick = {
                            scope.launch {
                                mainViewModel.imageLoader.memoryCache.clear()
                                context.cacheDir.deleteRecursively()
                                delay(350)
                                cacheCleaned = true
                                val size = getFolderSize(context.cacheDir)
                                cacheDirectorySize = convertSize(size.toInt())
                                delay(4000)
                                cacheCleaned = false
                            }
                        }
                    )
                }
                item {
                    SettingsItem(
                        title = "Check for update",
                        subtitle = buildAnnotatedString {
                            val showLatest =
                                mainViewModel.updateStatus == "update_available" || mainViewModel.updateStatus == "update_required" || mainViewModel.updateStatus == "latest"

                            append("Current version: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("v${BuildConfig.VERSION_NAME}\n")
                            }
                            append("Latest version: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                if (showLatest)
                                    append("v${mainViewModel.latestVersion.versionName}\n\n")
                                else
                                    append("···\n\n")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                when (mainViewModel.updateStatus) {
                                    "checking" -> append("Checking for update...")
                                    "update_available" -> append("Update available, tap here to download")
                                    "update_required" -> append("Update available, tap here to download")
                                    "latest" -> append("You're using the latest version!")
                                    "failed" -> append("Failed to check for update, please check your internet connection")
                                }
                            }
                        },
                        onClick = {
                            if (mainViewModel.updateStatus == "update_available" || mainViewModel.updateStatus == "update_required") {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/uragiristereo/Mejiboard/releases/tag/v${mainViewModel.latestVersion.versionName}")
                                )
                                context.startActivity(intent)
                            } else
                                scope.launch {
                                    delay(350)
                                    mainViewModel.updateStatus = "checking"
                                    delay(1000)
                                    mainViewModel.checkForUpdate()
                                }
                        }
                    )
                }
            }
        }
    }
}