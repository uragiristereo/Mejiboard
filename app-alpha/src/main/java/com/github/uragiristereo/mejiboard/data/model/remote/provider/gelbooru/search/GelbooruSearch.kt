package com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.search

import com.squareup.moshi.Json

data class GelbooruSearch(
    val category: String,
    val label: String,

    @field:Json(name = "post_count")
    val postCount: Int,

    val type: String,
    val value: String,
)
