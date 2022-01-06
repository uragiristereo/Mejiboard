package com.github.uragiristereo.mejiboard.data.dto.api.tag


data class TagDto(
    val ambiguous: Int,
    val count: Int,
    val id: Int,
    val name: String,
    val type: Int,
)