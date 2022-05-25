package com.github.uragiristereo.mejiboard.domain.entity.provider.post

data class PostsResult(
    val data: List<Post> = emptyList(),
    val canLoadMore: Boolean = true,
    val errorMessage: String = "",
)
