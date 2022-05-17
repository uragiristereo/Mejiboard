package com.github.uragiristereo.mejiboard.presentation.posts.core

import com.github.uragiristereo.mejiboard.domain.entity.Post

data class PostsState(
    val posts: MutableList<Post> = mutableListOf(),
    val loading: Boolean = true,
    val error: String = "",
    val page: Int = 0,
    val newSearch: Boolean = true,

    // saved state
    val jumpToPosition: Boolean = false,

    // top app bar
    val toolbarOffsetHeightPx: Float = 0f,
    val browseHeightPx: Float = 0f,

    // bottom app bar
    val moreDropDownExpanded: Boolean = false,

    // scaffold
    val confirmExit: Boolean = true,

    // image post
    val allowPostClick: Boolean = true,
)
