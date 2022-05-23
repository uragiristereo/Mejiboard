package com.github.uragiristereo.mejiboard.data.model.remote.app

data class AppUpdate(
    val variant: String,
    val releases: List<ReleaseInfo>,
)