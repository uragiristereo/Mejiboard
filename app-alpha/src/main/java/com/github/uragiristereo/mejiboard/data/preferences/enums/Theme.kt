package com.github.uragiristereo.mejiboard.data.preferences.enums

import kotlinx.serialization.Serializable

@Serializable
sealed class Theme(val text: String) {
    @Serializable
    object System : Theme(text = "System default")

    @Serializable
    object Light : Theme(text = "Light")

    @Serializable
    object Dark : Theme(text = "Dark")

    override fun toString(): String {
        return this.text
    }
}