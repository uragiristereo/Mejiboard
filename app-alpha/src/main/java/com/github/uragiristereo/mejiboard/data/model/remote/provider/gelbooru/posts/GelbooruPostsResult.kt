package com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.posts

import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.common.Attributes
import com.squareup.moshi.Json

data class GelbooruPostsResult(
    @field:Json(name = "@attributes")
    val attributes: Attributes,

    val post: List<GelbooruPost>?,
)
