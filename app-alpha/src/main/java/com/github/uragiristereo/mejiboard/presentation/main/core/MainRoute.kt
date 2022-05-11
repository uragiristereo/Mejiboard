package com.github.uragiristereo.mejiboard.presentation.main.core

sealed class MainRoute(
    private val route: String,
    val Key: String = "",
) {
    object Main : MainRoute(route = "main")
    object Search : MainRoute(route = "search")
    object Settings : MainRoute(route = "settings")
    object Image : MainRoute(route = "image", Key = "post")
    object About : MainRoute(route = "about")

    override fun toString(): String {
        return when {
            Key.isNotEmpty() -> "$route/{$Key}"
            else -> route
        }
    }
}
