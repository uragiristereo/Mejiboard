package com.github.uragiristereo.mejiboard.common.extension

import com.github.uragiristereo.mejiboard.data.dto.api.PostDto
import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.data.dto.api.TagDto
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.domain.entity.Search
import com.github.uragiristereo.mejiboard.domain.entity.Tag

fun PostDto.toPost() = Post(
    createdAt = createdAt,
    directory = directory,
    hash = hash,
    height = height,
    id = id,
    image = image,
    owner = owner,
    previewHeight = previewHeight,
    previewWidth = previewWidth,
    rating = rating,
    sample = sample,
    sampleHeight = sampleHeight,
    sampleWidth = sampleWidth,
    source = source,
    tags = tags,
    width = width,
)

fun SearchDto.toSearch() = Search(
    postCount = postCount,
    value = value,
)

fun TagDto.toTag() = Tag(
    count = count,
    id = id,
    tag = tag,
    type = type,
)