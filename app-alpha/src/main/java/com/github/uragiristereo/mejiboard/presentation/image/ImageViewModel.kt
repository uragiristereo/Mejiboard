package com.github.uragiristereo.mejiboard.presentation.image

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.uragiristereo.mejiboard.presentation.image.core.ImageState
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    val exoPlayerCache: SimpleCache,
) : ViewModel() {
    val state = mutableStateOf(ImageState())
}