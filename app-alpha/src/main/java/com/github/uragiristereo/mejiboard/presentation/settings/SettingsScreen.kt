package com.github.uragiristereo.mejiboard.presentation.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.settings.core.SettingsTopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val ENABLE_SAFE_LISTING_TOGGLE = false

@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val columnState = rememberLazyListState()
    val systemUiController = rememberSystemUiController()
    val state by viewModel.state

    val surfaceColor = MaterialTheme.colors.surface
    val isLight = MaterialTheme.colors.isLight

    val smallHeaderOpacity by animateFloatAsState(
        targetValue = if (state.useBigHeader) 0f else 1f,
        animationSpec = tween(durationMillis = 350),
    )
    val bigHeaderOpacity by animateFloatAsState(
        targetValue = if (state.useBigHeader) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
    )

    LaunchedEffect(key1 = Unit) {
        launch { viewModel.getFormattedFolderSize(context.cacheDir) }

        launch {
            while (true) {
                if (state.settingsHeaderSize > 0) {
                    viewModel.shouldUseBigHeader(
                        columnState = columnState,
                        onPerformScroll = {
                            launch { columnState.animateScrollToItem(index = it) }
                        },
                    )
                }

                delay(timeMillis = 100L)
            }
        }

        mainViewModel.checkForUpdate()
    }

    DisposableEffect(key1 = surfaceColor, key2 = isLight) {
        systemUiController.apply {
            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                setStatusBarColor(Color.Black)
                setNavigationBarColor(surfaceColor)
            } else {
                setStatusBarColor(Color.Transparent, isLight)
                setNavigationBarColor(surfaceColor.copy(0.4f))
            }
        }

        onDispose { }
    }

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                state = state,
                smallHeaderOpacity = smallHeaderOpacity,
                onBackArrowClick = { mainNavigation.navigateUp() },
            )
        },
        bottomBar = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(insets = WindowInsets.navigationBars)
            )
        },
    ) {
        SettingItemList(
            state = state,
            columnState = columnState,
            bigHeaderOpacity = bigHeaderOpacity,
            innerPadding = WindowInsets.navigationBars.asPaddingValues(),
            mainViewModel = mainViewModel,
        )
    }
}