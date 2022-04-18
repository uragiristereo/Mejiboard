package com.github.uragiristereo.mejiboard.presentation.image.more

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.entity.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var post by mutableStateOf<Post?>(null)
        private set

    init {
        savedStateHandle.get<Post>(Constants.STATE_KEY_SELECTED_POST).let { post = it }
    }
}