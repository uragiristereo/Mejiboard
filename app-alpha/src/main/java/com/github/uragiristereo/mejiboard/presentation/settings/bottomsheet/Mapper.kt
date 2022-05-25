package com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders

fun ApiProviders.toPreferenceItem(): PreferenceItem {
    return PreferenceItem(
        key = value,
        title = name,
        subtitle = domain,
    )
}