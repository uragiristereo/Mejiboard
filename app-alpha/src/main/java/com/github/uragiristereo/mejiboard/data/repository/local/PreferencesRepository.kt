package com.github.uragiristereo.mejiboard.data.repository.local

import android.content.Context
import androidx.datastore.dataStore
import com.github.uragiristereo.mejiboard.domain.entity.preferences.AppPreferences
import com.github.uragiristereo.mejiboard.data.local.preferences.AppPreferencesSerializer
import com.github.uragiristereo.mejiboard.data.model.local.Ref
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
    val permissionState = Ref("")
    var blockFromRecents = Ref(false)

    suspend fun update(newData: AppPreferences) {
        appDataStore.updateData { newData }
    }

    fun getInitialTheme(): String {
        return runBlocking { appDataStore.data.map { it.theme }.first() }
    }
}