package com.github.uragiristereo.mejiboard.data.repository

import android.content.Context
import androidx.datastore.dataStore
import com.github.uragiristereo.mejiboard.data.model.Reference
import com.github.uragiristereo.mejiboard.data.preferences.AppPreferences
import com.github.uragiristereo.mejiboard.data.preferences.AppPreferencesSerializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class PreferencesRepository(context: Context) {
    private val Context.protoDataStore by dataStore(
        fileName = "app-settings.json",
        serializer = AppPreferencesSerializer,
    )

    private val appDataStore = context.protoDataStore
    val data = appDataStore.data

    // deprecated
    val permissionState = Reference("")
    var blockFromRecents = Reference(false)

    suspend fun update(newData: AppPreferences) {
        appDataStore.updateData { newData }
    }

    fun getInitialTheme(): String {
        return runBlocking { appDataStore.data.map { it.theme }.first() }
    }
}