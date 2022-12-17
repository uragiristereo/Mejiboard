package com.github.uragiristereo.mejiboard.presentation.main

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.data.model.local.preferences.Theme
import com.github.uragiristereo.mejiboard.presentation.common.theme.MejiboardTheme
import kotlinx.coroutines.launch
import java.io.File

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
    val window = remember { (context as Activity).window }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val isSystemDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(key1 = mainViewModel.preferences.theme) {
        mainViewModel.isDesiredThemeDark =
            when (mainViewModel.preferences.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                else -> isSystemDarkTheme
            }
    }

    LaunchedEffect(key1 = Unit) {
        val tempDirectory = File("${context.cacheDir.absolutePath}/temp/")

        scope.launch { tempDirectory.deleteRecursively() }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (mainViewModel.preferences.blockFromRecents) {
                        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    MejiboardTheme(
        theme = mainViewModel.preferences.theme,
        blackTheme = mainViewModel.preferences.blackTheme,
        content = {
            Surface(color = MaterialTheme.colors.background) {
                CompositionLocalProvider(
                    values = arrayOf(
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