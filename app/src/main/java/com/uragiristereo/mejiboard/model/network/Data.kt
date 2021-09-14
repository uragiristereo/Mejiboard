package com.uragiristereo.mejiboard.model

import java.util.*

data class Post(
    val id: Int,
    val image: String,
    val directory: String,
    val width: Int,
    val height: Int,
    val sample_width: Int,
    val sample_height: Int,
    val preview_height: Int,
    val preview_width: Int,
    val sample: Int,
    val tags: String,
    val owner: String,
    val source: String,
    val rating: String,
    val created_at: Date
)

data class Search(
    val value: String,
    val post_count: Int
)

data class Tag(
    val id: Int,
    val tag: String,
    val type: String,
    val count: Int
)
