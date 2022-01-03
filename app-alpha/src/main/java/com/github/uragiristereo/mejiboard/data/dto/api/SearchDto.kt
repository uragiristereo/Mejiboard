package com.github.uragiristereo.mejiboard.data.dto.api


import com.squareup.moshi.Json

data class SearchDto(
    val category: String,
    val label: String,
    @field:Json(name = "post_count")
    val postCount: String,
    val type: String,
    val value: String,
)