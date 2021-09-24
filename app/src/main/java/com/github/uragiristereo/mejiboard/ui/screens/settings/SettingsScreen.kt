package com.github.uragiristereo.mejiboard.ui.screens.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.ui.components.SettingsCategory
import com.github.uragiristereo.mejiboard.ui.components.SettingsItem
import com.github.uragiristereo.mejiboard.ui.components.SettingsOptions
import com.github.uragiristereo.mejiboard.ui.components.SettingsOptionsItem
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.DNS_OVER_HTTPS_PROVIDER
import com.github.uragiristereo.mejiboard.util.PREVIEW_SIZE
import com.github.uragiristereo.mejiboard.util.SAFE_LISTING_ONLY
import com.github.uragiristereo.mejiboard.util.USE_DNS_OVER_HTTPS
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                title = {
                    Text("Settings")
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
    ) { innerPadding ->
        Surface(
            Modifier.padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsCategory(text = "Interface")
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
                Divider()
                SettingsCategory(text = "Behavior")

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
//                        append(" rated posts")
                        append(" rated posts\n(DISABLED)")
                        append(" on ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("alpha")
                        }
                        append(" release")
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

                Divider()
                SettingsCategory(text = "Advanced")

                val dohInteractionSource = remember { MutableInteractionSource() }
                SettingsItem(
                    title = "DNS over HTTPS",
                    subtitle = buildAnnotatedString {
                        append("Enable if Gelbooru is blocked in your country ")

                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append("(recommended)")
//                            append("(recommended)\nRestart")
                        }
//                        append(" is required to takes effect")
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
                Divider()
                SettingsCategory(text = "Miscellaneous")

                SettingsItem(
                    title = "Clear cache",
                    subtitle = "Remove all cached memory and files",
                    onClick = {
                        scope.launch {
                            mainViewModel.imageLoader.memoryCache.clear()
                            context.cacheDir.deleteRecursively()
                            Toast.makeText(context, "Cache successfully cleaned", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                Spacer(
                    Modifier
                        .navigationBarsHeight()
                        .fillMaxWidth()
                )
            }
        }
    }
}