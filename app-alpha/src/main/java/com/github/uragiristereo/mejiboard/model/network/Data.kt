package com.github.uragiristereo.mejiboard.model.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Post(
    val id: Int,
    val hash: String,
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
) : Parcelable

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

data class DownloadInfo(
    val progress: Float = 0f,
    val downloaded: Long = 0L,
    val length: Long = 0L,
    var path: String = "",
    var status: String = "idle"
)

data class AppUpdate(
    val variant: String,
    val releases: List<ReleaseInfo>
)

data class ReleaseInfo(
    val versionCode: Int,
    val versionName: String,
    val updateRequired: Boolean
)