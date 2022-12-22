package com.github.uragiristereo.mejiboard.presentation.main.core

import com.github.uragiristereo.mejiboard.presentation.common.navigation.NavigationRoute

sealed class MainRoute(
    route: String,
    argsKeys: List<String> = listOf(),
) : NavigationRoute(route, argsKeys) {
    object Posts : MainRoute(
        route = "posts",
        argsKeys = listOf("tags"),
    )

    object Search : MainRoute(
        route = "search",
        argsKeys = listOf("tags"),
    )

    object Image : MainRoute(
        route = "image",
        argsKeys = listOf("post"),
    )

    object Settings : MainRoute(route = "settings")

    object About : MainRoute(route = "about")
}
