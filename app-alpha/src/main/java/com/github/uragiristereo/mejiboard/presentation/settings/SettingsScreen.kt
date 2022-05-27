package com.github.uragiristereo.mejiboard.presentation.settings

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet.SettingsBottomSheet
import com.github.uragiristereo.mejiboard.presentation.settings.core.SettingsTopAppBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val columnState = rememberLazyListState()
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val systemUiController = rememberSystemUiController()
    val scope = rememberCoroutineScope()
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

    val useBigHeader by remember {
        derivedStateOf {
            viewModel.shouldUseBigHeader(
                columnState = columnState,
                onPerformScroll = {
                    scope.launch { columnState.animateScrollToItem(index = it) }
                },
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getFormattedFolderSize(context.cacheDir)

        mainViewModel.checkForUpdate()
    }

    LaunchedEffect(key1 = surfaceColor, key2 = isLight) {
        systemUiController.apply {
            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                setStatusBarColor(Color.Black)
                setNavigationBarColor(surfaceColor)
            } else {
                setStatusBarColor(Color.Transparent, isLight)
                setNavigationBarColor(surfaceColor.copy(0.4f))
            }
        }
    }

    LaunchedEffect(key1 = useBigHeader) {
        viewModel.state.update { it.copy(useBigHeader = useBigHeader) }
    }

    BackHandler(
        enabled = bottomSheetState.isVisible,
        onBack = {
            scope.launch {
                bottomSheetState.animateTo(targetValue = ModalBottomSheetValue.Hidden)
            }
        }
    )

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
                    .windowInsetsBottomHeight(insets = WindowInsets.navigationBars),
            )
        },
    ) {
        SettingItemList(
            state = state,
            bottomSheetState = bottomSheetState,
            columnState = columnState,
            bigHeaderOpacity = bigHeaderOpacity,
            innerPadding = WindowInsets.navigationBars.asPaddingValues(),
            mainViewModel = mainViewModel,
        )
    }

    viewModel.state.value.selectedBottomSheetData?.let {
        SettingsBottomSheet(
            state = bottomSheetState,
            data = it,
        )
    }
}