package com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet

import com.github.uragiristereo.mejiboard.common.RatingFilter
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating

fun ApiProvider.toPreferenceItem(): PreferenceItem {
    return PreferenceItem(
        key = key,
        title = name,
        subtitle = domain,
    )
}

fun Pair<String, List<Rating>>.toPreferenceItem(): PreferenceItem {
    return PreferenceItem(
        key = first,
        title = when (second) {
            RatingFilter.GENERAL_ONLY -> "General only (mostly SFW)"
            RatingFilter.SAFE -> "General & sensitive (default)"
            RatingFilter.NO_EXPLICIT -> "Filter explicit"
            RatingFilter.UNFILTERED -> "Unfiltered"
            else -> "General only"
        }
    )
}