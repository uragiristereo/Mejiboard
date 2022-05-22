package com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.tags

import com.squareup.moshi.Json
import java.util.*

data class DanbooruTag(
    val id: Int,
    val name: String,

    @field:Json(name = "post_count")
    val postCount: Int,

    val category: Int,

    @field:Json(name = "created_at")
    val createdAt: Date,

    @field:Json(name = "updated_at")
    val updatedAt: Date,

    @field:Json(name = "is_locked")
    val isLocked: Boolean,

    @field:Json(name = "is_deprecated")
    val isDeprecated: Boolean,
)
