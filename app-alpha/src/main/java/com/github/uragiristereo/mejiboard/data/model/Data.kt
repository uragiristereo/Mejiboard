package com.github.uragiristereo.mejiboard.data.model

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