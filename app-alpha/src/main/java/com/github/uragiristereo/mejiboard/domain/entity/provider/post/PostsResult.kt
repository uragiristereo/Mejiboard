package com.github.uragiristereo.mejiboard.domain.entity.provider.post

data class PostsResult(
    val data: List<Post> = emptyList(),
    val statusCode: Int = 200,
    val errorMessage: String = "",
)
