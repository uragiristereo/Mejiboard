package com.github.uragiristereo.mejiboard.util

import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*

class CustomDateAdapter : JsonAdapter<Date>() {
    private val dateFormat = SimpleDateFormat(SERVER_FORMAT, Locale.US)

    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        return try {
            val dateAsString = reader.nextString()
            synchronized(dateFormat) {
                dateFormat.parse(dateAsString)
            }
        } catch (e: Exception) {
            null
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            synchronized(dateFormat) {
                writer.value(value.toString())
            }
        }
    }

    companion object {
        const val SERVER_FORMAT = ("EEE MMM dd HH:mm:ss ZZZ yyyy") // define your server format here
    }
}