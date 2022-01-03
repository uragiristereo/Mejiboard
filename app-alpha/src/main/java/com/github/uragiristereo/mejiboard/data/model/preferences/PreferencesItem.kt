package com.github.uragiristereo.mejiboard.data.model.preferences

import androidx.datastore.preferences.core.Preferences

data class PreferencesItem<T>(
    val key: Preferences.Key<T>,
    val default: T,
)