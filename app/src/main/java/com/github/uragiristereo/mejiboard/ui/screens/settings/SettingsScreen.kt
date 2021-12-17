package com.github.uragiristereo.mejiboard.ui.screens.settings

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
import com.github.uragiristereo.mejiboard.ui.components.SettingsCategory
import com.github.uragiristereo.mejiboard.ui.components.SettingsItem
import com.github.uragiristereo.mejiboard.ui.components.SettingsOptions
import com.github.uragiristereo.mejiboard.ui.components.SettingsOptionsItem
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.*
import com.github.uragiristereo.mejiboard.util.FileHelper.convertSize
import com.github.uragiristereo.mejiboard.util.FileHelper.getFolderSize
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()

    val themes = listOf(
        SettingsOptionsItem("system", "System default"),
        SettingsOptionsItem("dark", "Dark"),
        SettingsOptionsItem("light", "Light")
    )
    val selectedThemeFromViewModel = themes.filter { it.key == mainViewModel.theme }[0]
    var selectedTheme by remember { mutableStateOf(selectedThemeFromViewModel) }

    val previewSizes = listOf(
        SettingsOptionsItem("sample", "Compressed (sample)"),
        SettingsOptionsItem("original", "Full size (original)")
    )
    val selectedPreviewSizeFromViewModel = previewSizes.filter { it.key == mainViewModel.previewSize }[0]
    var selectedPreviewSize by remember { mutableStateOf(selectedPreviewSizeFromViewModel) }

    val dohProviders = listOf(
        SettingsOptionsItem("cloudflare", "Cloudflare"),
        SettingsOptionsItem("google", "Google"),
        SettingsOptionsItem("tuna", "Tuna (for China Mainland)")
    )
    val selectedDohProviderFromViewModel = dohProviders.filter { it.key == mainViewModel.dohProvider }[0]
    var selectedDohProvider by remember { mutableStateOf(selectedDohProviderFromViewModel) }

    var cacheDirectorySize by remember { mutableStateOf("Loading...") }
    var cacheCleaned by remember { mutableStateOf(false) }
    var settingsHeaderSize by remember { mutableStateOf(0) }
    var bigHeader by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
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
                        subtitle = selectedTheme.value,
                        items = themes,
                        selectedItemKey = selectedTheme.key,
                        onItemSelected = {
                            selectedTheme = it
                            mainViewModel.setTheme(selectedTheme.key, mainViewModel.blackTheme)
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
                                    mainViewModel.blackTheme = !mainViewModel.blackTheme
                                    mainViewModel.setTheme(
                                        if (mainViewModel.isDesiredThemeDark) "dark" else "light",
                                        mainViewModel.blackTheme
                                    )
                                },
                                action = {
                                    Switch(
                                        checked = mainViewModel.blackTheme,
                                        interactionSource = blackThemeInteractionSource,
                                        onCheckedChange = {
                                            mainViewModel.blackTheme = !mainViewModel.blackTheme
                                            mainViewModel.setTheme(
                                                if (mainViewModel.isDesiredThemeDark) "dark" else "light",
                                                mainViewModel.blackTheme
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
                        subtitle = selectedPreviewSize.value,
                        items = previewSizes,
                        selectedItemKey = selectedPreviewSize.key,
                        onItemSelected = {
                            selectedPreviewSize = it
                            mainViewModel.previewSize = it.key
                            mainViewModel.save(PREVIEW_SIZE, it.key)
                        }
                    )
                }
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
                        enabled = false,
                        interactionSource = safeListingOnlyInteractionSource,
                        onClick = {
                            mainViewModel.safeListingOnly = !mainViewModel.safeListingOnly
                            mainViewModel.refreshNeeded = true
                            mainViewModel.save(SAFE_LISTING_ONLY, mainViewModel.safeListingOnly)
                        },
                        action = {
                            Switch(
                                checked = mainViewModel.safeListingOnly,
                                enabled = false,
                                interactionSource = safeListingOnlyInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.safeListingOnly = !mainViewModel.safeListingOnly
                                    mainViewModel.refreshNeeded = true
                                    mainViewModel.save(SAFE_LISTING_ONLY, mainViewModel.safeListingOnly)
                                }
                            )
                        }
                    )
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
                            mainViewModel.dohEnabled = !mainViewModel.dohEnabled
                            mainViewModel.save(USE_DNS_OVER_HTTPS, mainViewModel.dohEnabled)
                            mainViewModel.renewNetworkInstance(mainViewModel.dohEnabled, mainViewModel.dohProvider)
                            mainViewModel.refreshNeeded = true
                        },
                        interactionSource = dohInteractionSource,
                        action = {
                            Switch(
                                checked = mainViewModel.dohEnabled,
                                interactionSource = dohInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.dohEnabled = !mainViewModel.dohEnabled
                                    mainViewModel.save(USE_DNS_OVER_HTTPS, mainViewModel.dohEnabled)
                                    mainViewModel.renewNetworkInstance(mainViewModel.dohEnabled, mainViewModel.dohProvider)
                                    mainViewModel.refreshNeeded = true
                                }
                            )
                        }
                    )
                }
                item {
                    AnimatedContent(targetState = mainViewModel.dohEnabled) { target ->
                        if (target)
                            SettingsOptions(
                                title = "DNS over HTTPS Provider",
                                subtitle = selectedDohProvider.value,
                                items = dohProviders,
                                selectedItemKey = selectedDohProvider.key,
                                onItemSelected = {
                                    selectedDohProvider = it
                                    mainViewModel.dohProvider = it.key
                                    mainViewModel.save(DNS_OVER_HTTPS_PROVIDER, mainViewModel.dohProvider)
                                    mainViewModel.renewNetworkInstance(mainViewModel.dohEnabled, mainViewModel.dohProvider)
                                    mainViewModel.refreshNeeded = true
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
                            mainViewModel.blockFromRecents = !mainViewModel.blockFromRecents
                            mainViewModel.save(BLOCK_CONTENT_FROM_RECENTS, mainViewModel.blockFromRecents)
                        },
                        interactionSource = blockFromRecentsInteractionSource,
                        action = {
                            Switch(
                                checked = mainViewModel.blockFromRecents,
                                interactionSource = blockFromRecentsInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.blockFromRecents = !mainViewModel.blockFromRecents
                                    mainViewModel.save(BLOCK_CONTENT_FROM_RECENTS, mainViewModel.blockFromRecents)
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
                            mainViewModel.autoCleanCache = !mainViewModel.autoCleanCache
                            mainViewModel.save(AUTO_CLEAN_CACHE, mainViewModel.autoCleanCache)
                        },
                        interactionSource = autoCleanCacheInteractionSource,
                        action = {
                            Switch(
                                checked = mainViewModel.autoCleanCache,
                                interactionSource = autoCleanCacheInteractionSource,
                                onCheckedChange = {
                                    mainViewModel.autoCleanCache = !mainViewModel.autoCleanCache
                                    mainViewModel.save(AUTO_CLEAN_CACHE, mainViewModel.autoCleanCache)
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
                            val showLatest = mainViewModel.updateStatus == "update_available" || mainViewModel.updateStatus == "update_required" || mainViewModel.updateStatus == "latest"

                            append("Current version: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("v${BuildConfig.VERSION_NAME}\n")
                            }
                            append("Latest version: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                if (showLatest)
                                    append("${mainViewModel.latestVersion.versionName}\n\n")
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
                                    Uri.parse("https://github.com/uragiristereo/Mejiboard/releases/tag/${mainViewModel.latestVersion.versionName}")
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