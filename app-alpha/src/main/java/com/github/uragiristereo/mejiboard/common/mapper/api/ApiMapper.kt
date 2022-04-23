package com.github.uragiristereo.mejiboard.common.mapper.api

import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.domain.entity.Search

fun List<SearchDto>.toSearch(): List<Search> {
    return this.map {
        Search(
            postCount = it.postCount,
            value = it.value,
        )
    }
}