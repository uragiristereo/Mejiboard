package com.github.uragiristereo.mejiboard.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.about.AboutScreen
import com.github.uragiristereo.mejiboard.presentation.image.ImageScreen
import com.github.uragiristereo.mejiboard.presentation.posts.MainScreen
import com.github.uragiristereo.mejiboard.presentation.search.SearchScreen
import com.github.uragiristereo.mejiboard.presentation.settings.SettingsScreen
import com.github.uragiristereo.mejiboard.presentation.splash.SplashScreen
import com.github.uragiristereo.mejiboard.presentation.theme.MejiboardTheme
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
    val isSystemDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(key1 = mainViewModel.isDesiredThemeDark) {
        mainViewModel.isDesiredThemeDark =
            when (mainViewModel.theme) {
                "light" -> false
                "dark" -> true
                "system" -> isSystemDarkTheme
                else -> false
            }
    }

    LaunchedEffect(key1 = Unit) {

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
                MaterialMotionNavHost(navController = mainNavigation, startDestination = "main") {
                    composable("splash") {
                        val systemDarkTheme = isSystemInDarkTheme()

                        SideEffect {
                            systemUiController.setSystemBarsColor(if (systemDarkTheme) Color.Black else Color.White)
                        }
                        SplashScreen(mainViewModel, if (isSystemInDarkTheme()) Color.Black else Color.White)
                    }
                    composable(
                        "main",
                        exitMotionSpec = { _, target ->
                            when (target.destination.route) {
                                "image" -> materialElevationScaleOut()
                                else -> materialSharedAxisZOut()
                            }
                        },
                        popEnterMotionSpec = { initial, _ ->
                            when (initial.destination.route) {
                                "image" -> materialElevationScaleIn()
                                else -> materialSharedAxisZIn()
                            }
                        },
                    ) {
                        val surfaceColor = MaterialTheme.colors.surface
                        val isLight = MaterialTheme.colors.isLight

                        SideEffect {
                            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                                systemUiController.setStatusBarColor(Color.Black)
                                systemUiController.setNavigationBarColor(surfaceColor)
                            } else {
                                if (mainViewModel.isDesiredThemeDark)
                                    systemUiController.setStatusBarColor(Color.Black)
                                else
                                    systemUiController.setStatusBarColor(surfaceColor)
                                systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = isLight, navigationBarContrastEnforced = false)
                            }
                        }

                        MainScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "search",
                        enterMotionSpec = { _, _ ->
                            holdIn()
                        },
                    ) {
                        val surfaceColor = MaterialTheme.colors.surface

                        SideEffect {
                            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                                systemUiController.setStatusBarColor(Color.Black)
                                systemUiController.setNavigationBarColor(surfaceColor)
                            } else
                                systemUiController.setSystemBarsColor(surfaceColor)
                        }

                        SearchScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "settings",
                    ) {
                        val surfaceColor = MaterialTheme.colors.surface
                        val isLight = MaterialTheme.colors.isLight

                        SideEffect {
                            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                                systemUiController.setStatusBarColor(Color.Black)
                                systemUiController.setNavigationBarColor(surfaceColor)
                            } else {
                                systemUiController.setStatusBarColor(Color.Transparent, isLight)
                                systemUiController.setNavigationBarColor(surfaceColor.copy(0.4f))
                            }
                        }

                        SettingsScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "image",
                        enterMotionSpec = { _, _ ->
                            translateYIn({ it }, 250)
                        },
                        popExitMotionSpec = { _, _ ->
                            translateYOut({ it }, 250)
                        }
                    ) {
                        SideEffect {
                            systemUiController.setSystemBarsColor(Color.Black.copy(0.4f))
                        }
                        ImageScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "about",
                    ) {
                        val surfaceColor = MaterialTheme.colors.surface

                        SideEffect {
                            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                                systemUiController.setStatusBarColor(Color.Black)
                                systemUiController.setNavigationBarColor(surfaceColor)
                            } else {
                                systemUiController.setStatusBarColor(surfaceColor)
                                systemUiController.setNavigationBarColor(surfaceColor.copy(0.4f))
                            }
                        }

                        AboutScreen(mainNavigation, mainViewModel)
                    }
                }
            }

            AnimatedVisibility(
                visible = !mainViewModel.splashShown,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                SplashScreen(
                    mainViewModel = mainViewModel,
                    backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                )
            }
        }
    }
}