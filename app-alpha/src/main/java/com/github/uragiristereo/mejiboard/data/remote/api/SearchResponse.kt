package com.github.uragiristereo.mejiboard.data.remote.api

import com.squareup.moshi.Json

data class SearchResponse(
    val category: String,
    val label: String,
    @field:Json(name = "post_count")
    val postCount: String,
    val type: String,
    val value: String,
)
