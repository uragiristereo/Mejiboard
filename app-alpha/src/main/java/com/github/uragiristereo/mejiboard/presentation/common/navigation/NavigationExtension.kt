package com.github.uragiristereo.mejiboard.presentation.common.navigation

import android.net.Uri
import android.util.Base64
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import com.google.gson.Gson
import soup.compose.material.motion.EnterMotionSpec
import soup.compose.material.motion.ExitMotionSpec
import soup.compose.material.motion.navigation.composable


fun Any.toJson(gson: Gson = Gson()): String {
    val out = gson.toJson(this)
        .toByteArray()

    return Uri.encode(Base64.encodeToString(out, Base64.DEFAULT))
}

inline fun <reified T> String.fromJson(gson: Gson = Gson()): T? {
    val decoded = Base64.decode(this, Base64.DEFAULT)
        .toString(charset("UTF-8"))

    return gson.fromJson(decoded, T::class.java)
}

fun NavHostController.navigate(
    route: NavigationRoute,
    data: Map<String, Any> = mapOf(),
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    val uri = route.parseData(data)

    navigate(
        request = NavDeepLinkRequest.Builder
            .fromUri(NavDestination.createRoute(uri).toUri())
            .build(),
        navOptions = navOptions,
        navigatorExtras = navigatorExtras,
    )
}

fun NavHostController.navigate(
    route: NavigationRoute,
    data: Map<String, Any> = mapOf(),
    builder: NavOptionsBuilder.() -> Unit,
) {
    navigate(
        route = route,
        data = data,
        navOptions = navOptions(builder),
    )
}
//
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composable(
    route: NavigationRoute,
    deepLinks: List<NavDeepLink> = listOf(),
    content: @Composable NavigationRoute.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route.route,
        arguments = route.getNamedNavArgs(),
        deepLinks = deepLinks,
        content = { content(route, it) },
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composable(
    route: NavigationRoute,
    deepLinks: List<NavDeepLink> = listOf(),
    enterMotionSpec: (AnimatedContentScope<NavBackStackEntry>.() -> EnterMotionSpec?)? = null,
    exitMotionSpec: (AnimatedContentScope<NavBackStackEntry>.() -> ExitMotionSpec?)? = null,
    popEnterMotionSpec: (AnimatedContentScope<NavBackStackEntry>.() -> EnterMotionSpec?)? = enterMotionSpec,
    popExitMotionSpec: (AnimatedContentScope<NavBackStackEntry>.() -> ExitMotionSpec?)? = exitMotionSpec,
    content: @Composable NavigationRoute.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route.route,
        arguments = route.getNamedNavArgs(),
        deepLinks = deepLinks,
        enterMotionSpec = enterMotionSpec,
        exitMotionSpec = exitMotionSpec,
        popEnterMotionSpec = popEnterMotionSpec,
        popExitMotionSpec = popExitMotionSpec,
        content = { content(route, it) },
    )
}