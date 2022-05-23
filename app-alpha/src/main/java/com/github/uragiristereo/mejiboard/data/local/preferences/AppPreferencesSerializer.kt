package com.github.uragiristereo.mejiboard.data.local.preferences

import androidx.datastore.core.Serializer
import com.squareup.moshi.Moshi
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object AppPreferencesSerializer : Serializer<AppPreferences> {
    override val defaultValue: AppPreferences
        get() = AppPreferences()
    private val adapter = Moshi.Builder()
        .build()
        .adapter(AppPreferences::class.java)

    override suspend fun readFrom(input: InputStream): AppPreferences {
        return try {
            adapter.fromJson(input.readBytes().decodeToString()) ?: defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppPreferences, output: OutputStream) {
        val out = adapter.toJson(t)

        output.write(out.encodeToByteArray())
    }
}