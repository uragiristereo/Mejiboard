package com.github.uragiristereo.mejiboard.data.preferences

import com.github.uragiristereo.mejiboard.data.preferences.enums.DohProvider
import com.github.uragiristereo.mejiboard.data.preferences.enums.PreviewSize
import com.github.uragiristereo.mejiboard.data.preferences.enums.Theme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppPreferences(
    val theme: Theme = Theme.System,
    @SerialName(value = "black_theme")
    val blackTheme: Boolean = false,
    @SerialName(value = "preview_size")
    val previewSize: PreviewSize = PreviewSize.Sample,
    @SerialName(value = "safe_listing_only")
    val safeListingOnly: Boolean = true,
    @SerialName(value = "doh_enabled")
    val useDnsOverHttps: Boolean = true,
    @SerialName(value = "doh_provider")
    val dohProvider: DohProvider = DohProvider.Cloudflare,
    @SerialName(value = "auto_clean_cache")
    val autoCleanCache: Boolean = true,
    @SerialName(value = "block_from_recents")
    val blockFromRecents: Boolean = true,
    @SerialName(value = "remind_later_counter")
    val remindLaterCounter: Int = -1,
    @SerialName(value = "video_volume")
    val videoVolume: Float = 0.5f,
)
