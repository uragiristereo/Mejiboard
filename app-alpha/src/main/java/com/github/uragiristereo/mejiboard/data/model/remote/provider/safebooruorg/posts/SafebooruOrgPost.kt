package com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.posts

import com.squareup.moshi.Json

data class SafebooruOrgPost(
    val directory: String,
    val hash: String,
    val height: Int,
    val id: Int,
    val image: String,
    val change: Int,
    val owner: String,

    @field:Json(name = "parent_id")
    val parentId: Int,

    val rating: String,
    val sample: Boolean,

    @field:Json(name = "sample_height")
    val sampleHeight: Int,

    @field:Json(name = "sample_width")
    val sampleWidth: Int,

    val score: Int?,
    val tags: String,
    val width: Int,
)
