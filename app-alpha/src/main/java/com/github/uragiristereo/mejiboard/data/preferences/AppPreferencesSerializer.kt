package com.github.uragiristereo.mejiboard.data.preferences

import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object AppPreferencesSerializer : Serializer<AppPreferences> {
    override val defaultValue: AppPreferences
        get() = AppPreferences()

    override suspend fun readFrom(input: InputStream): AppPreferences {
        return try {
            Json.decodeFromString(
                deserializer = AppPreferences.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppPreferences, output: OutputStream) {
        val out = Json.encodeToString(
            serializer = AppPreferences.serializer(),
            value = t,
        )

        output.write(out.encodeToByteArray())
    }
}