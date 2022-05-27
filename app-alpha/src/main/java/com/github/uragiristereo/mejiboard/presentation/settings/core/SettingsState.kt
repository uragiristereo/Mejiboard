package com.github.uragiristereo.mejiboard.presentation.settings.core

import com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet.SettingsBottomSheetData

data class SettingsState(
    val cacheDirectorySize: String = "Loading...",
    val isCacheCleaned: Boolean = false,
    val settingsHeaderSize: Int = 0,
    val useBigHeader: Boolean = true,
    val selectedBottomSheetData: SettingsBottomSheetData? = null,
)
