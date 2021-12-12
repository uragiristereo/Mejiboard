package com.github.uragiristereo.mejiboard.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.ui.screens.about.AboutScreen
import com.github.uragiristereo.mejiboard.ui.screens.image.ImageScreen
import com.github.uragiristereo.mejiboard.ui.screens.posts.MainScreen
import com.github.uragiristereo.mejiboard.ui.screens.search.SearchScreen
import com.github.uragiristereo.mejiboard.ui.screens.settings.SettingsScreen
import com.github.uragiristereo.mejiboard.ui.screens.splash.SplashScreen
import com.github.uragiristereo.mejiboard.ui.theme.MejiboardTheme
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.MiuiHelper
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import soup.compose.material.motion.*
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import java.io.File

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun MainNavigation(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainNavigation = rememberMaterialMotionNavController()
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    mainViewModel.isDesiredThemeDark =
        when (mainViewModel.theme) {
            "light" -> false
            "dark" -> true
            "system" -> isSystemInDarkTheme()
            else -> false
        }

    LaunchedEffect(true) {
        val tempDirectory = File(context.cacheDir.absolutePath + "/temp/")
        tempDirectory.deleteRecursively()
    }

    ProvideWindowInsets(
        windowInsetsAnimationsEnabled = true
    ) {
        MejiboardTheme(
            theme = mainViewModel.theme,
            blackTheme = mainViewModel.blackTheme
        ) {
            Surface(color = MaterialTheme.colors.background) {
                MaterialMotionNavHost(navController = mainNavigation, startDestination = if (mainViewModel.splashShown) "main" else "splash") {
                    composable("splash") {
//                        systemUiController.setSystemBarsColor(if (mainViewModel.isDesiredThemeDark) MaterialTheme.colors.background else MaterialTheme.colors.primaryVariant)
                        systemUiController.setSystemBarsColor(if (isSystemInDarkTheme()) Color.Black else Color.White)
                        mainViewModel.saveSplashShown()
                        SplashScreen(mainNavigation, mainViewModel, if (isSystemInDarkTheme()) Color.Black else Color.White)
                    }
                    composable(
                        "main",
                        exitMotionSpec = { _, _ ->
                            holdOut()
                        },
                        popEnterMotionSpec = { initial, _ ->
                            if (initial.destination.route == "image")
                                holdIn()
                            else
                                materialSharedAxisZIn()
                        },
                    ) {
                        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else {
                            if (mainViewModel.isDesiredThemeDark)
                                systemUiController.setStatusBarColor(Color.Black)
                            else
                                systemUiController.setStatusBarColor(MaterialTheme.colors.surface)
                            systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = MaterialTheme.colors.isLight, navigationBarContrastEnforced = false)
                        }

                        MainScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "search",
                        enterMotionSpec = { _, _ ->
                            holdIn()
                        },
                    ) {
                        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else
                            systemUiController.setSystemBarsColor(MaterialTheme.colors.surface)

                        SearchScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "settings",
                    ) {
                        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else {
                            systemUiController.setStatusBarColor(Color.Transparent, MaterialTheme.colors.isLight)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface.copy(0.4f))
                        }

                        SettingsScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "image",
                        enterMotionSpec = { _, _ ->
                            translateYIn({ it }, 200)
                        },
                        popExitMotionSpec = { _, _ ->
                            translateYOut({ it }, 200)
                        }
                    ) {
                        systemUiController.setSystemBarsColor(Color.Black.copy(0.4f))
                        ImageScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "about",
                    ) {
                        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else {
                            systemUiController.setStatusBarColor(MaterialTheme.colors.surface)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface.copy(0.4f))
                        }

                        AboutScreen(mainNavigation, mainViewModel)
                    }
                }
            }
        }
    }
}