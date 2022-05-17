package com.github.uragiristereo.mejiboard.presentation.posts.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostsSavedState(
    val loadFromSession: Boolean = false,
    val scrollIndex: Int = 0,
    val scrollOffset: Int = 0,
): Parcelable
