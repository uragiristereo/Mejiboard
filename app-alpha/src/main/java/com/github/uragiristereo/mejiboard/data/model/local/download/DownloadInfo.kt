package com.github.uragiristereo.mejiboard.data.model.local.download

data class DownloadInfo(
    val progress: Float = 0f,
    val downloaded: Long = 0L,
    val length: Long = 0L,
    var path: String = "",
    var status: String = "idle"
)