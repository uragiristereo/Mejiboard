package com.github.uragiristereo.mejiboard.domain.entity.provider.tag

data class TagsResult(
    val data: List<Tag> = emptyList(),
    val statusCode: Int = 200,
    val errorMessage: String = "",
)
