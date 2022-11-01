package com.github.uragiristereo.mejiboard.presentation.posts.core

import androidx.compose.runtime.Immutable
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider

data class PostsState(
    val tags: String = "",
    val initialized: Boolean = false,
    val loading: Boolean = true,
    val error: String = "",
    val page: Int = 0,
    val canLoadMore: Boolean = true,
    val lastFabVisible: Boolean = false,

    // saved state
    val jumpToPosition: Boolean = false,

    // bottom app bar
    val moreDropDownExpanded: Boolean = false,

    // scaffold
    val confirmExit: Boolean = true,

    // image post
    val allowPostClick: Boolean = true,

    // provider
    val selectedProvider: ApiProvider = ApiProviders.Gelbooru,
)


@Immutable
data class ImmutableList<T>(
    val value: List<T>
)

fun <T> immutableListOf(value: List<T> = listOf()): ImmutableList<T> {
    return ImmutableList(value)
}