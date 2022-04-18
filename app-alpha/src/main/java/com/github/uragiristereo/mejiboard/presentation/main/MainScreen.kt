package com.github.uragiristereo.mejiboard.presentation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.data.preferences.enums.Theme
import com.github.uragiristereo.mejiboard.presentation.theme.MejiboardTheme
import com.google.accompanist.insets.ProvideWindowInsets
import java.io.File

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isSystemDarkTheme = isSystemInDarkTheme()
    val preferences = mainViewModel.preferences

    LaunchedEffect(key1 = preferences.theme) {
        mainViewModel.isDesiredThemeDark =
            when (preferences.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                Theme.System -> isSystemDarkTheme
            }
    }

    LaunchedEffect(key1 = Unit) {
        val tempDirectory = File("${context.cacheDir.absolutePath}/temp/")

        tempDirectory.deleteRecursively()
    }

    ProvideWindowInsets(
        windowInsetsAnimationsEnabled = false,
    ) {
        MejiboardTheme(
            theme = preferences.theme,
            blackTheme = preferences.blackTheme,
        ) {
            Surface(color = MaterialTheme.colors.background) {
                MainNavGraph(mainViewModel = mainViewModel)
            }
        }
    }
}