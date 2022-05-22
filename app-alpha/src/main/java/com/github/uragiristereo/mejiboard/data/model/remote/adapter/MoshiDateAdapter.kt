package com.github.uragiristereo.mejiboard.data.model.remote.adapter

import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*

class MoshiDateAdapter constructor(pattern: String): JsonAdapter<Date>() {
    private val dateFormat = SimpleDateFormat(pattern, Locale.US)

    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        return try {
            when {
                reader.peek() != JsonReader.Token.NULL -> dateFormat.parse(reader.nextString())
                else -> {
                    reader.nextNull<Unit>()
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        value?.let {
            writer.value(it.toString())
        }
    }
}