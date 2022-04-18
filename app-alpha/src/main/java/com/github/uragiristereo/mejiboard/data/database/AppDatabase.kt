package com.github.uragiristereo.mejiboard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.uragiristereo.mejiboard.data.database.entity.session.SessionDao
import com.github.uragiristereo.mejiboard.data.database.entity.session.SessionPost

@Database(
    entities = [
        SessionPost::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}