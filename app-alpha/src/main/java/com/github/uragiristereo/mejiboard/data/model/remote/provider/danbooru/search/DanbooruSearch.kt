package com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.search

import com.squareup.moshi.Json

data class DanbooruSearch(
    val type: String,
    val label: String,
    val value: String,
    val category: Int,

    @field:Json(name = "post_count")
    val postCount: Int,

    val antecedent: String?,
)
