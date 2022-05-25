package com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet

import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider

fun ApiProvider.toPreferenceItem(): PreferenceItem {
    return PreferenceItem(
        key = key,
        title = name,
        subtitle = domain,
    )
}