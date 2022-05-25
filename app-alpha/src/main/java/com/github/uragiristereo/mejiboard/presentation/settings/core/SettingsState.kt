package com.github.uragiristereo.mejiboard.presentation.settings.core

import com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet.PreferenceItem

data class SettingsState(
    val cacheDirectorySize: String = "Loading...",
    val isCacheCleaned: Boolean = false,
    val settingsHeaderSize: Int = 0,
    val useBigHeader: Boolean = true,
    val bottomSettingItems: List<PreferenceItem> = emptyList(),
    val selectedBottomSetting: PreferenceItem? = null,
)
