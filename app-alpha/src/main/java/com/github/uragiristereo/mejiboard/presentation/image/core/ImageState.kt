package com.github.uragiristereo.mejiboard.presentation.image.core

import com.github.uragiristereo.mejiboard.domain.entity.Post

data class ImageState(
    val selectedPost: Post? = null,
    val appBarVisible: Boolean = true,
    val imageSize: String = "",
    val originalImageSize: String = "",
    val originalImageShown: Boolean = false,
    val showOriginalImage: Boolean = false,
    val isPressed: Boolean = false,

    // image post specific state
    val offsetY: Float = 0f,
    val animatedOffsetY: Float = 0f,
    val currentZoom: Float = 1f,
    val imageLoading: Boolean = true,
    val fingerCount: Int = 1,

    // video post specific state
    val volumeSliderVisible: Boolean = false,
    val isVideoHasAudio: Boolean = false,
)