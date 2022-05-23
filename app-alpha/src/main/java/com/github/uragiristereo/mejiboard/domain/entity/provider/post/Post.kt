package com.github.uragiristereo.mejiboard.domain.entity.provider.post

import java.util.*

data class Post(
    val type: String,
    val id: Int,
    val scaled: Boolean,
    val rating: Rating,
    val tags: String,
    val uploadedAt: Date,
    val uploader: String,
    val source: String,
    val originalImage: ImagePost,
    val scaledImage: ImagePost,
    val previewImage: ImagePost,
)
