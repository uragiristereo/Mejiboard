package com.github.uragiristereo.mejiboard.data.dto.api.tag

import com.github.uragiristereo.mejiboard.data.dto.api.common.Attributes
import com.squareup.moshi.Json

data class TagResultDto(
    @field:Json(name = "@attributes")
    val attributes: Attributes,
    val tag: List<TagDto>?,
)
