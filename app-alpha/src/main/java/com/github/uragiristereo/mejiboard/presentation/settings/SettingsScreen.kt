package com.github.uragiristereo.mejiboard.presentation.settings

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
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
    onNavigateUp: () -> Unit,
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

    LaunchedEffect(key1 = Unit) {
        viewModel.getFormattedFolderSize(context.cacheDir)

        mainViewModel.checkForUpdate()
    }

    LaunchedEffect(key1 = surfaceColor) {
        systemUiController.apply {
            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                setStatusBarColor(Color.Black)
                setNavigationBarColor(surfaceColor)
            } else {
                setStatusBarColor(surfaceColor)
                setNavigationBarColor(surfaceColor.copy(0.4f))
            }
        }
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
            SettingsTopAppBar(onNavigateUp = onNavigateUp)
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
            isCacheCleaned = state.isCacheCleaned,
            cacheDirectorySize = state.cacheDirectorySize,
            bottomSheetState = bottomSheetState,
            columnState = columnState,
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