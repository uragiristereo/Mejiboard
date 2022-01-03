package com.github.uragiristereo.mejiboard.data.dto.api.post

import com.squareup.moshi.Json
import java.util.*

data class PostDto(
    val change: Int,
    @field:Json(name = "created_at")
    val createdAt: Date,
    @field:Json(name = "creator_id")
    val creatorId: Int,
    val directory: String,
    @field:Json(name = "file_url")
    val fileUrl: String,
    @field:Json(name = "has_children")
    val hasChildren: String,
    @field:Json(name = "has_comments")
    val hasComments: String,
    @field:Json(name = "has_notes")
    val hasNotes: String,
    val height: Int,
    val id: Int,
    val image: String,
    val md5: String,
    val owner: String,
    @field:Json(name = "parent_id")
    val parentId: Int,
    @field:Json(name = "post_locked")
    val postLocked: Int,
    @field:Json(name = "preview_height")
    val previewHeight: Int,
    @field:Json(name = "preview_url")
    val previewUrl: String,
    @field:Json(name = "preview_width")
    val previewWidth: Int,
    val rating: String,
    val sample: Int,
    @field:Json(name = "sample_height")
    val sampleHeight: Int,
    @field:Json(name = "sample_width")
    val sampleWidth: Int,
    val score: Int,
    val source: Any,
    val tags: String,
    val title: Any,
    val width: Int,
)
