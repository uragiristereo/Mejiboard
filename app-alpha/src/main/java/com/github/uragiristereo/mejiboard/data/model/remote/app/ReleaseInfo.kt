package com.github.uragiristereo.mejiboard.data.model.remote.app

data class ReleaseInfo(
    val versionCode: Int,
    val versionName: String,
    val updateRequired: Boolean,
)