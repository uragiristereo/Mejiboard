package com.github.uragiristereo.mejiboard.presentation.common.extension

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.navigation.*
import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.squareup.moshi.Moshi

inline fun <reified T> NavController.navigate(
    route: String,
    data: Pair<String, T>,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    val key = "{${data.first}}"
    val count = route
        .split(key)
        .size
        .dec()

    if (count != 1) {
        throw IllegalArgumentException()
    }

    val out = Moshi.Builder()
        .add(MoshiDateAdapter(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
        .build()
        .adapter(T::class.java)
        .toJson(data.second)
    val newRoute = route.replace(
        oldValue = key,
        newValue = Uri.encode(out),
    )

    navigate(
        request = NavDeepLinkRequest.Builder
            .fromUri(NavDestination.createRoute(route = newRoute).toUri())
            .build(),
        navOptions = navOptions,
        navigatorExtras = navigatorExtras,
    )
}

inline fun <reified T> NavBackStackEntry.getData(key: String): T? {
    val data = arguments?.getString(key)

    return when {
        data != null -> Moshi.Builder()
            .add(MoshiDateAdapter(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
            .build()
            .adapter(T::class.java)
            .fromJson(data)
        else -> null
    }
}

@Composable
inline fun <reified T> NavBackStackEntry.rememberGetData(key: String): T? {
    return remember { getData<T>(key) }
}