package com.github.uragiristereo.mejiboard.presentation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.data.model.local.preferences.Theme
import com.github.uragiristereo.mejiboard.presentation.main.core.FixedInsets
import com.github.uragiristereo.mejiboard.presentation.common.theme.MejiboardTheme
import kotlinx.coroutines.launch
import java.io.File

val LocalFixedInsets = compositionLocalOf<FixedInsets> { error("no FixedInsets provided!") }
val LocalMainViewModel = compositionLocalOf<MainViewModel> { error("no MainViewModel provided!") }

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isSystemDarkTheme = isSystemInDarkTheme()
    val preferences = mainViewModel.preferences

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    val fixedInsets = remember {
        FixedInsets(
            statusBarHeight = systemBarsPadding.calculateTopPadding(),
            navigationBarsPadding = PaddingValues(
                bottom = systemBarsPadding.calculateBottomPadding(),
                start = systemBarsPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = systemBarsPadding.calculateEndPadding(LayoutDirection.Ltr),
            ),
        )
    }

    LaunchedEffect(key1 = preferences.theme) {
        mainViewModel.isDesiredThemeDark =
            when (preferences.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                else -> isSystemDarkTheme
            }
    }

    LaunchedEffect(key1 = Unit) {
        val tempDirectory = File("${context.cacheDir.absolutePath}/temp/")

        scope.launch { tempDirectory.deleteRecursively() }
    }

    MejiboardTheme(
        theme = preferences.theme,
        blackTheme = preferences.blackTheme,
        content = {
            Surface(color = MaterialTheme.colors.background) {
                CompositionLocalProvider(
                    values = arrayOf(
                        LocalFixedInsets provides fixedInsets,
                        LocalMainViewModel provides mainViewModel,
                    ),
                    content = {
                        MainNavGraph(mainViewModel = mainViewModel)
                    },
                )
            }
        }
    )
}