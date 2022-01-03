package com.github.uragiristereo.mejiboard.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Post(
    val createdAt: Date,
    val directory: String,
    val hash: String,
    val height: Int,
    val id: Int,
    val image: String,
    val owner: String,
    val previewHeight: Int,
    val previewWidth: Int,
    val rating: String,
    val sample: Int,
    val sampleHeight: Int,
    val sampleWidth: Int,
    val source: String,
    val tags: String,
    val width: Int,
) : Parcelable
