package com.github.uragiristereo.mejiboard.data.dto.app

data class AppUpdate(
    val variant: String,
    val releases: List<ReleaseInfo>,
)