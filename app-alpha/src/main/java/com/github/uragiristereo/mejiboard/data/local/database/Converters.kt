package com.github.uragiristereo.mejiboard.data.local.database

import androidx.room.TypeConverter
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.ImagePost
import com.squareup.moshi.Moshi
import java.util.*

class Converters {
    private val adapter = Moshi.Builder()
        .build()
        .adapter(ImagePost::class.java)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun imagePostToString(value: ImagePost): String {
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toImagePost(value: String): ImagePost? {
        return adapter.fromJson(value)
    }
}