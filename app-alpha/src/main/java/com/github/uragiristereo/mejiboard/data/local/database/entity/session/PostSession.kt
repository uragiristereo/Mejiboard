package com.github.uragiristereo.mejiboard.data.local.database.entity.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.ImagePost
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.squareup.moshi.Json
import java.util.*

@Entity(tableName = "session")
data class PostSession(
    @PrimaryKey
    val id: Int,

    val provider: String,
    val scaled: Boolean,
    val rating: Rating,
    val tags: String,
    val uploadedAt: Date,
    val uploader: String,
    val source: String,

    @ColumnInfo(name = "original_image")
    @field:Json(name = "original_image")
    val originalImage: ImagePost,

    @ColumnInfo(name = "scaled_image")
    @field:Json(name = "scaled_image")
    val scaledImage: ImagePost,

    @ColumnInfo(name = "preview_image")
    @field:Json(name = "preview_image")
    val previewImage: ImagePost,

    val sequence: Int,
)