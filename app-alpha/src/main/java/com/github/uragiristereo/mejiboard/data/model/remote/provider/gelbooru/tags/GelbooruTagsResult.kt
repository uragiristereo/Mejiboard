package com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.tags

import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.common.Attributes
import com.squareup.moshi.Json

data class GelbooruTagsResult(
    @field:Json(name = "@attributes")
    val attributes: Attributes,

    val tag: List<GelbooruTag>?,
)