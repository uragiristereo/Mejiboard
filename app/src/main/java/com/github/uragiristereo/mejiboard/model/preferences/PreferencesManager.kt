package com.github.uragiristereo.mejiboard.model.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


class PreferencesManager(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val dataStore: DataStore<Preferences> = context.dataStore
    private var permissionState = ""

    fun setPermissionState(state: String) {
        permissionState = state
    }

    fun getPermissionState(): String {
        return permissionState
    }
}