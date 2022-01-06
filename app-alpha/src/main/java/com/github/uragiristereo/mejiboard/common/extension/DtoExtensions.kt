package com.github.uragiristereo.mejiboard.common.extension

import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.data.dto.api.tag.TagDto
import com.github.uragiristereo.mejiboard.data.dto.api.post.PostDto
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.domain.entity.Search
import com.github.uragiristereo.mejiboard.domain.entity.Tag

fun PostDto.toPost(): Post {
    val fixedSource =
        try { (source) as String }
        catch (e: Exception) { "" }

    return Post(
        createdAt = createdAt,
        directory = directory,
        hash = md5,
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
        source = fixedSource,
        tags = tags,
        width = width,
    )
}

fun SearchDto.toSearch() = Search(
    postCount = postCount,
    value = value,
)

fun TagDto.toTag() = Tag(
    count = count,
    id = id,
    name = name,
    type = type,
)