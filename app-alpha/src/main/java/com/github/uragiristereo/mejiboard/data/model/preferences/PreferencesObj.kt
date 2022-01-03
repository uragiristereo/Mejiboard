package com.github.uragiristereo.mejiboard.data.model.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesObj {
    val theme = PreferencesItem(stringPreferencesKey("THEME"), "system")
    val blackTheme = PreferencesItem(booleanPreferencesKey("BLACK_DARK_THEME"), false)
    val previewSize = PreferencesItem(stringPreferencesKey("PREVIEW_SIZE"), "sample")
    val safeListingOnly = PreferencesItem(booleanPreferencesKey("SAFE_LISTING_ONLY"), true)
    val dohEnabled = PreferencesItem(booleanPreferencesKey("USE_DNS_OVER_HTTPS"), true)
    val dohProvider = PreferencesItem(stringPreferencesKey("DNS_OVER_HTTPS_PROVIDER"), "cloudflare")
    val autoCleanCache = PreferencesItem(booleanPreferencesKey("AUTO_CLEAN_CACHE"), true)
    val blockFromRecents = PreferencesItem(booleanPreferencesKey("BLOCK_CONTENT_FROM_RECENTS"), true)
    val remindLaterCounter = PreferencesItem(intPreferencesKey("REMIND_LATER_UPDATE_COUNTER"), -1)
    val videoVolume = PreferencesItem(floatPreferencesKey("VIDEO_VOLUME"), 0.5f)
}