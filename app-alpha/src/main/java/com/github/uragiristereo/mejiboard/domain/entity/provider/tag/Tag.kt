package com.github.uragiristereo.mejiboard.domain.entity.provider.tag

data class Tag(
    val id: Int,
    val name: String,
    val count: Int,
    val type: TagType,
)