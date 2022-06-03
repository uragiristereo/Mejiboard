package com.github.uragiristereo.mejiboard.data.model.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDatabaseMigration {
    val MIGRATE_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS session")
            database.execSQL("CREATE TABLE IF NOT EXISTS `session` (" +
                    "`id` INTEGER NOT NULL," +
                    "`provider` TEXT NOT NULL," +
                    "`scaled` INTEGER NOT NULL," +
                    "`rating` TEXT NOT NULL, " +
                    "`tags` TEXT NOT NULL, " +
                    "`uploadedAt` INTEGER NOT NULL, " +
                    "`uploader` TEXT NOT NULL, " +
                    "`source` TEXT NOT NULL, " +
                    "`original_image` TEXT NOT NULL, " +
                    "`scaled_image` TEXT NOT NULL, " +
                    "`preview_image` TEXT NOT NULL, " +
                    "`sequence` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`)" +
                    ")")
        }
    }
}