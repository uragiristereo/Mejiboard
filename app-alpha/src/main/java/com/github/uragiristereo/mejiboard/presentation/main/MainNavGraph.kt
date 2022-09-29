package com.github.uragiristereo.mejiboard.presentation.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.presentation.about.AboutScreen
import com.github.uragiristereo.mejiboard.presentation.common.extension.rememberGetData
import com.github.uragiristereo.mejiboard.presentation.common.materialFadeIn
import com.github.uragiristereo.mejiboard.presentation.common.materialFadeOut
import com.github.uragiristereo.mejiboard.presentation.common.materialSharedAxisZNoFadeIn
import com.github.uragiristereo.mejiboard.presentation.common.materialSharedAxisZNoFadeOut
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
                startDestination = "${MainRoute.Posts}",
            ) {
                composable(
                    route = "${MainRoute.Posts}",
                    arguments = listOf(
                        navArgument(name = MainRoute.Posts.Key) {
                            type = NavType.StringType
                            defaultValue = MainRoute.Posts.defaultValue
                        },
                    ),
                    enterMotionSpec = {
                        when (initialState.destination.route) {
                            "${MainRoute.Posts}" -> materialFadeIn()
                            "${MainRoute.Search}" -> holdIn()
                            else -> materialSharedAxisZNoFadeIn()
                        }
                    },
                    exitMotionSpec = {
                        when (targetState.destination.route) {
                            "${MainRoute.Posts}" -> materialFadeOut()
                            "${MainRoute.Image}" -> holdOut()
                            "${MainRoute.Search}" -> holdOut()
                            else -> materialSharedAxisZNoFadeOut()
                        }
                    },
                    popEnterMotionSpec = {
                        when (initialState.destination.route) {
                            "${MainRoute.Image}" -> holdIn()
                            "${MainRoute.Search}" -> materialFadeIn()
                            else -> materialSharedAxisZNoFadeIn()
                        }
                    },
                    popExitMotionSpec = {
                        when (targetState.destination.route) {
                            "${MainRoute.Image}" -> holdOut()
                            "${MainRoute.Search}" -> holdOut()
                            else -> materialSharedAxisZNoFadeOut()
                        }
                    },
                    content = { entry ->
                        val tags =
                            remember { entry.arguments?.getString(MainRoute.Posts.Key) ?: MainRoute.Posts.defaultValue }

                        PostsScreen(
                            tags = tags,
                            mainNavigation = mainNavigation,
                            mainViewModel = mainViewModel,
                        )
                    },
                )

                composable(
                    route = "${MainRoute.Search}",
                    arguments = listOf(
                        navArgument(name = MainRoute.Search.Key) {
                            type = NavType.StringType
                            defaultValue = MainRoute.Search.defaultValue
                        },
                    ),
                    enterMotionSpec = {
                        holdIn()
                    },
                    popExitMotionSpec = {
                        materialFadeOut()
                    },
                    content = { entry ->
                        val tags =
                            remember { entry.arguments?.getString(MainRoute.Search.Key) ?: MainRoute.Search.defaultValue }

                        SearchScreen(
                            tags = tags,
                            mainNavigation = mainNavigation,
                            mainViewModel = mainViewModel,
                        )
                    },
                )

                composable(
                    route = "${MainRoute.Settings}",
                    enterMotionSpec = {
                        materialSharedAxisZNoFadeIn()
                    },
                    exitMotionSpec = {
                        materialSharedAxisZNoFadeOut()
                    },
                    popEnterMotionSpec = {
                        materialSharedAxisZNoFadeIn()
                    },
                    popExitMotionSpec = {
                        materialSharedAxisZNoFadeOut()
                    },
                    content = {
                        SettingsScreen(
                            onNavigateUp = {
                                mainNavigation.navigateUp()
                            },
                            mainViewModel = mainViewModel,
                        )
                    },
                )

                composable(
                    route = "${MainRoute.Image}",
                    arguments = listOf(
                        navArgument(name = MainRoute.Image.Key) { type = NavType.StringType },
                    ),
                    enterMotionSpec = {
                        translateYFadeIn(
                            initialOffsetY = { it / 5 },
                            durationMillis = 250,
                        )
                    },
                    popExitMotionSpec = {
                        if (mainViewModel.backPressedByGesture)
                            materialFadeOut(durationMillis = 350)
                        else
                            translateYFadeOut(
                                targetOffsetY = { it / 5 },
                                durationMillis = 250,
                            )
                    },
                    content = { entry ->
                        val selectedPost = entry.rememberGetData<Post>(key = MainRoute.Image.Key)

                        selectedPost?.let { post ->
                            ImageScreen(
                                post = post,
                                mainNavigation = mainNavigation,
                                mainViewModel = mainViewModel,
                            )
                        }
                    },
                )

                composable(
                    route = "${MainRoute.About}",
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