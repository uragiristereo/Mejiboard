package com.github.uragiristereo.mejiboard.common.mapper.api

import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.data.dto.api.tag.TagDto
import com.github.uragiristereo.mejiboard.domain.entity.Search
import com.github.uragiristereo.mejiboard.domain.entity.Tag

fun List<SearchDto>.toSearch(): List<Search> {
    return this.map {
        Search(
            postCount = it.postCount,
            value = it.value,
        )
    }
}

fun List<TagDto>.toTag(): List<Tag> {
    return this.map {
        Tag(
            count = it.count,
            id = it.id,
            name = it.name,
            type = it.type,
        )
    }
}