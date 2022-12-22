package com.github.uragiristereo.mejiboard.presentation.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

open class NavigationRoute(
    route: String,
    private val argsKeys: List<String> = listOf(),
) {
    val route = parseRoute(route, argsKeys)

    override fun toString(): String {
        return route
    }

    private fun parseRoute(route: String, keys: List<String>): String {
        var args = ""

        keys.forEach { key ->
            args += "&$key={$key}"
        }

        if (args.take(n = 1) == "&") {
            args = args.replaceFirst(oldChar = '&', newChar = '?')
        }

        return route + args
    }

    fun getNamedNavArgs(): List<NamedNavArgument> {
        return argsKeys.map { key ->
            navArgument(name = key) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        }
    }

    fun parseData(params: Map<String, Any>): String {
        var result = route

        params.forEach { (key, value) ->
            val arg = value.toJson()

            result = result.replace(oldValue = "{$key}", newValue = arg)
        }

        return result
    }

    inline fun <reified T> getData(entry: NavBackStackEntry, key: String): T? {
        val dataStr = entry.arguments?.getString(key) ?: return null

        return dataStr.fromJson()
    }

    @Composable
    inline fun <reified T> rememberGetData(entry: NavBackStackEntry, key: String): T? {
        return remember(entry) { getData(entry, key) }
    }
}