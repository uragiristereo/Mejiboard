package com.github.uragiristereo.mejiboard.presentation.common.mapper

import com.github.uragiristereo.mejiboard.data.local.database.entity.session.PostSession
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post

fun PostSession.toPost(): Post {
    return Post(
        provider = provider,
        id = id,
        scaled = scaled,
        rating = rating,
        tags = tags,
        uploadedAt = uploadedAt,
        uploader = uploader,
        source = source,
        originalImage = originalImage,
        scaledImage = scaledImage,
        previewImage = previewImage,
    )
}

fun Post.toPostSession(sequence: Int): PostSession {
    return PostSession(
        provider = provider,
        id = id,
        scaled = scaled,
        rating = rating,
        tags = tags,
        uploadedAt = uploadedAt,
        uploader = uploader,
        source = source,
        originalImage = originalImage,
        scaledImage = scaledImage,
        previewImage = previewImage,
        sequence = sequence,
    )
}