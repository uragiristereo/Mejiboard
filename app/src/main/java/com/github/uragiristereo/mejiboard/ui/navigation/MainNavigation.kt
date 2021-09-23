package com.github.uragiristereo.mejiboard.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.github.uragiristereo.mejiboard.ui.screens.about.AboutScreen
import com.github.uragiristereo.mejiboard.ui.screens.image.ImageScreen
import com.github.uragiristereo.mejiboard.ui.screens.posts.MainScreen
import com.github.uragiristereo.mejiboard.ui.screens.search.SearchScreen
import com.github.uragiristereo.mejiboard.ui.screens.settings.SettingsScreen
import com.github.uragiristereo.mejiboard.ui.screens.splash.SplashScreen
import com.github.uragiristereo.mejiboard.ui.theme.MejiboardTheme
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.MiuiHelper.isDeviceMiui
import soup.compose.material.motion.materialSharedAxisXIn
import soup.compose.material.motion.materialSharedAxisXOut
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController

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
    val isDeviceMiui = isDeviceMiui()

    mainViewModel.isDesiredThemeDark =
        when (mainViewModel.theme) {
            "light" -> false
            "dark" -> true
            "system" -> isSystemInDarkTheme()
            else -> false
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
                        systemUiController.setSystemBarsColor(if (mainViewModel.isDesiredThemeDark) MaterialTheme.colors.background else MaterialTheme.colors.primaryVariant)

                        SplashScreen(mainNavigation, mainViewModel.isDesiredThemeDark)
                    }
                    composable("main") {
                        if (isDeviceMiui && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else
                            systemUiController.setSystemBarsColor(MaterialTheme.colors.surface)

                        MainScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "search",
                        enterMotionSpec = { _, _ -> materialSharedAxisXIn() },
                        exitMotionSpec = { _, _ -> materialSharedAxisXOut() },
                    ) {
                        if (isDeviceMiui && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else
                            systemUiController.setSystemBarsColor(MaterialTheme.colors.surface)

                        SearchScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "settings",
                    ) {
                        if (isDeviceMiui && !mainViewModel.isDesiredThemeDark) {
                            systemUiController.setStatusBarColor(Color.Black)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface)
                        } else {
                            systemUiController.setStatusBarColor(MaterialTheme.colors.surface)
                            systemUiController.setNavigationBarColor(MaterialTheme.colors.surface.copy(0.4f))
                        }

                        SettingsScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "image",
                    ) {
                        systemUiController.setSystemBarsColor(Color.Black.copy(0.4f))
                        ImageScreen(mainNavigation, mainViewModel)
                    }
                    composable(
                        "about",
                    ) {
                        if (isDeviceMiui && !mainViewModel.isDesiredThemeDark) {
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