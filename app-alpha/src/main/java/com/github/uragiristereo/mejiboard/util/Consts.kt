package com.github.uragiristereo.mejiboard.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

//const val API_KEY = "api_key=ea02d007cf0a69a7bab6ea1f5f313adfe7c22de825dbeb57e7e5cb09bbb5d03a&user_id=762048"
const val BASE_URL = "https://gelbooru.com/"
val THEME = stringPreferencesKey("THEME")
val BLACK_DARK_THEME = booleanPreferencesKey("BLACK_DARK_THEME")
val PREVIEW_SIZE = stringPreferencesKey("PREVIEW_SIZE")
val SAFE_LISTING_ONLY = booleanPreferencesKey("SAFE_LISTING_ONLY")
val USE_DNS_OVER_HTTPS = booleanPreferencesKey("USE_DNS_OVER_HTTPS")
val DNS_OVER_HTTPS_PROVIDER = stringPreferencesKey("DNS_OVER_HTTPS_PROVIDER")
val AUTO_CLEAN_CACHE = booleanPreferencesKey("AUTO_CLEAN_CACHE")
val BLOCK_CONTENT_FROM_RECENTS = booleanPreferencesKey("BLOCK_CONTENT_FROM_RECENTS")
val REMIND_LATER_UPDATE_COUNTER = intPreferencesKey("REMIND_LATER_UPDATE_COUNTER")
val VIDEO_VOLUME = floatPreferencesKey("VIDEO_VOLUME")
const val STATE_KEY_SELECTED_POST = "main.selected.post"
const val STATE_KEY_NOTIFICATION_COUNT = "main.notification.count"
const val STATE_KEY_SPLASH_SHOWN = "main.splash.shown"