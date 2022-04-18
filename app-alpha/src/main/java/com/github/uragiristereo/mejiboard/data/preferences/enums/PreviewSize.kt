package com.github.uragiristereo.mejiboard.data.preferences.enums

import kotlinx.serialization.Serializable

@Serializable
sealed class PreviewSize(val name: String) {
    @Serializable
    object Sample : PreviewSize(name = "Compressed (sample)")

    @Serializable
    object Original : PreviewSize(name = "Full size (original)")

    override fun toString(): String {
        return this.name
    }
}