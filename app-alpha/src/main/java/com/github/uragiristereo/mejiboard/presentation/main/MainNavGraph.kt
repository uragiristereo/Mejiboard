package com.github.uragiristereo.mejiboard.presentation.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.presentation.about.AboutScreen
import com.github.uragiristereo.mejiboard.presentation.common.materialFadeIn
import com.github.uragiristereo.mejiboard.presentation.common.materialFadeOut
import com.github.uragiristereo.mejiboard.presentation.common.translateYFadeIn
import com.github.uragiristereo.mejiboard.presentation.common.translateYFadeOut
import com.github.uragiristereo.mejiboard.presentation.image.ImageScreen
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute
import com.github.uragiristereo.mejiboard.presentation.posts.PostsScreen
import com.github.uragiristereo.mejiboard.presentation.search.SearchScreen
import com.github.uragiristereo.mejiboard.presentation.settings.SettingsScreen
import com.github.uragiristereo.mejiboard.presentation.splash.SplashScreen
import soup.compose.material.motion.holdIn
import soup.compose.material.motion.holdOut
import soup.compose.material.motion.materialSharedAxisZIn
import soup.compose.material.motion.materialSharedAxisZOut
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun MainNavGraph(mainViewModel: MainViewModel) {
    val mainNavigation = rememberMaterialMotionNavController()

    Crossfade(targetState = mainViewModel.splashShown) { splashShown ->
        if (splashShown) {
            MaterialMotionNavHost(
                navController = mainNavigation,
                startDestination = MainRoute.Main.route,
            ) {
                composable(
                    route = MainRoute.Main.route,
                    enterMotionSpec = { initial, _ ->
                        when (initial.destination.route) {
                            MainRoute.Main.route -> materialFadeIn()
                            MainRoute.Search.route -> materialFadeIn()
                            else -> materialSharedAxisZIn()
                        }
                    },
                    exitMotionSpec = { _, target ->
                        when (target.destination.route) {
                            MainRoute.Image.route -> holdOut()
                            MainRoute.Search.route -> holdOut()
                            else -> materialSharedAxisZOut()
                        }
                    },
                    popEnterMotionSpec = { initial, _ ->
                        when (initial.destination.route) {
                            MainRoute.Image.route -> holdIn()
                            MainRoute.Search.route -> materialFadeIn()
                            else -> materialSharedAxisZIn()
                        }
                    },
                    content = { PostsScreen(mainNavigation, mainViewModel) },
                )

                composable(
                    route = MainRoute.Search.route,
                    enterMotionSpec = { _, _ ->
                        holdIn()
                    },
                    exitMotionSpec = { _, _ ->
                        materialFadeOut()
                    },
                    content = { SearchScreen(mainNavigation, mainViewModel) },
                )

                composable(
                    route = MainRoute.Settings.route,
                    content = { SettingsScreen(mainNavigation, mainViewModel) },
                )

                composable(
                    route = MainRoute.Image.route,
                    enterMotionSpec = { _, _ ->
//                        materialSharedAxisYIn(
//                            slideDistance = 48.dp,
//                            durationMillis = 300,
//                        )
                        translateYFadeIn(
                            initialOffsetY = { it / 5 },
                            durationMillis = 250,
                        )
                    },
                    popExitMotionSpec = { _, _ ->
                        if (mainViewModel.backPressedByGesture)
                            materialFadeOut(durationMillis = 350)
                        else
//                            materialSharedAxisYOut(
//                                slideDistance = 48.dp,
//                                durationMillis = 350,
//                            )
                            translateYFadeOut(
                                targetOffsetY = { it / 5 },
                                durationMillis = 250,
                            )
                    },
                    content = { ImageScreen(mainNavigation, mainViewModel) },
                )

                composable(
                    route = MainRoute.About.route,
                    content = { AboutScreen(mainNavigation, mainViewModel) },
                )
            }
        } else {
            SplashScreen(
                mainViewModel = mainViewModel,
                backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            )
        }
    }

}