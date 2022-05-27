package com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet

data class SettingsBottomSheetData(
    val title: String,
    val items: List<PreferenceItem>,
    val selectedItem: PreferenceItem?,
    val onItemSelected: (PreferenceItem) -> Unit,
)
