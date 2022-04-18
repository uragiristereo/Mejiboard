package com.github.uragiristereo.mejiboard.presentation.main.core

sealed class MainRoute(val route: String) {
    object Main : MainRoute(route = "main")
    object Search : MainRoute(route = "search")
    object Settings : MainRoute(route = "settings")
    object Image : MainRoute(route = "image")
    object About : MainRoute(route = "about")
}
