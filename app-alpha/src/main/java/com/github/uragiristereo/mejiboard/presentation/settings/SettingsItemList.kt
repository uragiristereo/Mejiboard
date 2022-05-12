package com.github.uragiristereo.mejiboard.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.data.preferences.enums.DohProvider
import com.github.uragiristereo.mejiboard.data.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.data.preferences.enums.Theme
import com.github.uragiristereo.mejiboard.presentation.common.SettingsCategory
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.settings.core.BigHeader
import com.github.uragiristereo.mejiboard.presentation.settings.core.SettingsState
import com.github.uragiristereo.mejiboard.presentation.settings.preference.DropDownPreference
import com.github.uragiristereo.mejiboard.presentation.settings.preference.RegularPreference
import com.github.uragiristereo.mejiboard.presentation.settings.preference.SwitchPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun SettingItemList(
    state: SettingsState,
    columnState: LazyListState,
    bigHeaderOpacity: Float,
    innerPadding: PaddingValues,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = mainViewModel.preferences

    val themes = remember { Theme::class.sealedSubclasses.map { it.objectInstance as Theme } }
    val previewSizes = remember { PreviewSize::class.sealedSubclasses.map { it.objectInstance as PreviewSize } }
    val dohProviders = remember { DohProvider::class.sealedSubclasses.map { it.objectInstance as DohProvider } }

    LazyColumn(
        state = columnState,
        contentPadding = innerPadding,
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            BigHeader(
                bigHeaderOpacity = bigHeaderOpacity,
                onSizeChange = { new ->
                    viewModel.state.update { it.copy(settingsHeaderSize = new) }
                },
            )
        }

        item { SettingsCategory(text = "Interface") }

        item {
            DropDownPreference(
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
                    SwitchPreference(
                        title = "Black dark theme",
                        subtitle = "Use pitch black theme instead of regular dark theme, useful for OLED screens",
                        checked = preferences.blackTheme,
                        onCheckedChange = {
                            mainViewModel.updatePreferences(newData = preferences.copy(blackTheme = it))
                        }
                    )
                }
            }
        }

        item { Divider() }

        item { SettingsCategory(text = "Behavior") }

        item {
            DropDownPreference(
                title = "Preview size",
                items = previewSizes,
                selectedItem = preferences.previewSize,
                onItemSelected = {
                    mainViewModel.updatePreferences(newData = preferences.copy(previewSize = it))
                }
            )
        }

        if (ENABLE_SAFE_LISTING_TOGGLE) {
            item {
                SwitchPreference(
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
                    checked = preferences.safeListingOnly,
                    onCheckedChange = {
                        mainViewModel.updatePreferences(newData = preferences.copy(safeListingOnly = it))
                        mainViewModel.refreshNeeded = true
                    }
                )
            }
        }

        item { Divider() }

        item { SettingsCategory(text = "Advanced") }

        item {
            SwitchPreference(
                title = "DNS over HTTPS",
                subtitle = buildAnnotatedString {
                    append("Enable if Gelbooru is blocked in your country ")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("(recommended)")
                    }
                },
                checked = preferences.useDnsOverHttps,
                onCheckedChange = {
                    mainViewModel.apply {
                        renewNetworkInstance(
                            useDnsOverHttps = preferences.useDnsOverHttps,
                            dohProvider = preferences.dohProvider.name,
                        )
                        updatePreferences(newData = preferences.copy(useDnsOverHttps = it))
                        refreshNeeded = true
                    }
                }
            )
        }
        item {
            DropDownPreference(
                enabled = preferences.useDnsOverHttps,
                title = "DNS over HTTPS provider",
                items = dohProviders,
                selectedItem = preferences.dohProvider,
                onItemSelected = {
                    mainViewModel.updatePreferences(newData = preferences.copy(dohProvider = it))
                },
            )
        }

        item {
            SwitchPreference(
                title = "Block content from Recent apps",
                subtitle = "Prevent content from showing in Recent apps",
                checked = preferences.blockFromRecents,
                onCheckedChange = {
                    mainViewModel.updatePreferences(newData = preferences.copy(blockFromRecents = it))
                }
            )
        }

        item { Divider() }

        item { SettingsCategory(text = "Miscellaneous") }

        item {
            SwitchPreference(
                title = "Auto clean cache",
                subtitle = "Automatically clean cache that older than 12 hours at startup",
                checked = preferences.autoCleanCache,
                onCheckedChange = {
                    mainViewModel.updatePreferences(newData = preferences.copy(autoCleanCache = it))
                }
            )
        }

        item {
            RegularPreference(
                title = "Clear cache now",
                subtitle = buildAnnotatedString {
                    append("Remove all cached memory and files\n")

                    if (state.isCacheCleaned) {
                        append("Cache successfully cleaned!")
                    } else {
                        append("Cache size: ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(state.cacheDirectorySize)
                        }

                        append(" (estimated)")
                    }
                },
                onClick = {
                    scope.launch {
                        context.apply {
                            imageLoader.memoryCache?.clear()
                            cacheDir.deleteRecursively()
                        }

                        delay(350)

                        viewModel.updateIsCacheCleanedState(file = context.cacheDir)
                    }
                }
            )
        }

        // TODO: refactor update
        item {
            RegularPreference(
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
                    } else {
                        scope.launch {
                            delay(350)
                            mainViewModel.updateStatus = "checking"
                            delay(1000)
                            mainViewModel.checkForUpdate()
                        }
                    }
                },
            )
        }
    }
}