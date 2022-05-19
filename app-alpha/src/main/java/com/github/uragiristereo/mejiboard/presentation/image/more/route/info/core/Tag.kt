package com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core

data class Tag(
    val id: Int,
    val name: String,
    val count: Int,
    val type: TermType,
)

enum class TermType {
    Artist,
    Character,
    Copyright,
    Metadata,
    Tag,
}