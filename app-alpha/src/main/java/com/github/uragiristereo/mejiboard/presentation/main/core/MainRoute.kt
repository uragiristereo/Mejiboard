package com.github.uragiristereo.mejiboard.presentation.main.core

sealed class MainRoute(
    private val route: String,
    val Key: String = "",
    private val optional: Boolean = false,
    val defaultValue: String = "",
) {
    object Posts : MainRoute(
        route = "posts",
        Key = "tags",
        defaultValue = "",
        optional = true,
    )
    object Search : MainRoute(
        route = "search",
        Key = "tags",
        defaultValue = "",
        optional = true,
    )
    object Settings : MainRoute(
        route = "settings",
    )
    object Image : MainRoute(
        route = "image",
        Key = "post",
    )
    object About : MainRoute(
        route = "about",
    )

    override fun toString(): String {
        return when {
            Key.isNotEmpty() && !optional -> "$route/{$Key}"
            Key.isNotEmpty() && optional -> "$route?$Key={$Key}"
            else -> route
        }
    }

    fun parseRoute(value: String): String {
        return when {
            Key.isNotEmpty() && optional -> "$route?$Key=$value"
            else -> route
        }
    }
}
