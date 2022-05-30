package com.github.uragiristereo.mejiboard.presentation.posts.core

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post

data class PostsState(
    val tags: String = "",
    val initialized: Boolean = false,
    val posts: MutableList<Post> = mutableListOf(),
    val loading: Boolean = true,
    val error: String = "",
    val page: Int = 0,
    val canLoadMore: Boolean = true,

    // saved state
    val jumpToPosition: Boolean = false,

    // top app bar
//    val toolbarOffsetHeightPx: Float = 0f,
//    val browseHeightPx: Float = 0f,

    // bottom app bar
    val moreDropDownExpanded: Boolean = false,

    // scaffold
    val confirmExit: Boolean = true,
    val lastFabVisible: Boolean = false,

    // image post
    val allowPostClick: Boolean = true,

    // provider
    val selectedProvider: ApiProvider = ApiProviders.Gelbooru,
)
