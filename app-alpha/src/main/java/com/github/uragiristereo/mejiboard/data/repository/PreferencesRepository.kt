package com.github.uragiristereo.mejiboard.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.github.uragiristereo.mejiboard.data.model.Reference
import com.github.uragiristereo.mejiboard.data.model.preferences.PreferencesItem
import com.github.uragiristereo.mejiboard.data.model.preferences.PreferencesObj
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PreferencesRepository(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore = context.dataStore
    val blockFromRecents = Reference(PreferencesObj.blockFromRecents.default)

    // deprecated
    val permissionState = Reference("")

    fun <T> readPreferences(
        scope: CoroutineScope,
        item: PreferencesItem<T>,
        onItemUpdated: (T) -> Unit,
    ) {
        dataStore.data.map { it[item.key] ?: item.default }
            .onEach { onItemUpdated(it) }
            .launchIn(scope)
    }

    fun <T> editPreferences(
        scope: CoroutineScope,
        item: PreferencesItem<T>,
        value: T,
    ) {
        scope.launch {
            dataStore.edit { it[item.key] = value }
        }
    }
}