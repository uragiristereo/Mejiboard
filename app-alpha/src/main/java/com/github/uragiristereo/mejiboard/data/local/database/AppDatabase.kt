package com.github.uragiristereo.mejiboard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.uragiristereo.mejiboard.data.local.database.entity.session.SessionDao
import com.github.uragiristereo.mejiboard.data.local.database.entity.session.PostSession

@Database(
    entities = [PostSession::class],
    version = 2,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}