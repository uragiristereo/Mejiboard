package com.github.uragiristereo.mejiboard.data.local.preferences

import com.github.uragiristereo.mejiboard.data.local.preferences.enums.DohProvider
import com.github.uragiristereo.mejiboard.data.local.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.data.local.preferences.enums.Theme
import com.squareup.moshi.Json


data class AppPreferences(
    val theme: String = Theme.System,

    @field:Json(name = "black_theme")
    val blackTheme: Boolean = false,

    @field:Json(name = "selected_provider")
    val provider: String = "gelboorusafe",

    @field:Json(name = "preview_size")
    val previewSize: String = PreviewSize.Sample,

    @field:Json(name = "doh_enabled")
    val useDnsOverHttps: Boolean = true,

    @field:Json(name = "doh_provider")
    val dohProvider: String = DohProvider.Cloudflare,

    @field:Json(name = "auto_clean_cache")
    val autoCleanCache: Boolean = true,

    @field:Json(name = "block_from_recents")
    val blockFromRecents: Boolean = false,

    @field:Json(name = "remind_later_counter")
    val remindLaterCounter: Int = -1,

    @field:Json(name = "video_volume")
    val videoVolume: Float = 0.5f,

    // deprecated
    @field:Json(name = "safe_listing_only")
    val safeListingOnly: Boolean = true,
)
