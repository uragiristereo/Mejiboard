package com.github.uragiristereo.mejiboard.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Bookmark(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date_added") val dateAdded: Date
)

@Entity
data class Blacklist(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "tag") val tag: String
)
