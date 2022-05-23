package com.github.uragiristereo.mejiboard.data.local.database.entity.session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "session")
data class SessionPost(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "createdAt")
    val createdAt: Date,
    @ColumnInfo(name = "tag")
    val directory: String,
    val hash: String,
    val height: Int,
    val image: String,
    val owner: String,
    @ColumnInfo(name = "preview_height")
    val previewHeight: Int,
    @ColumnInfo(name = "preview_width")
    val previewWidth: Int,
    val rating: String,
    val sample: Int,
    @ColumnInfo(name = "sample_height")
    val sampleHeight: Int,
    @ColumnInfo(name = "sample_width")
    val sampleWidth: Int,
    val source: String,
    val tags: String,
    val width: Int,
    val sequence: Int,
)