package com.github.uragiristereo.mejiboard.presentation.common.mapper

import androidx.compose.runtime.MutableState
import com.github.uragiristereo.mejiboard.presentation.settings.core.SettingsState


@JvmName("updateSettingsState")
inline fun MutableState<SettingsState>.update(function: (SettingsState) -> SettingsState) {
    value = function(value)
}