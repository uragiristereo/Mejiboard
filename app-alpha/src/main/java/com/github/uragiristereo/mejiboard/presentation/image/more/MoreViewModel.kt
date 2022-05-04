package com.github.uragiristereo.mejiboard.presentation.image.more

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.domain.usecase.api.CheckFileUseCase
import com.github.uragiristereo.mejiboard.domain.usecase.api.GetTagsInfoUseCase
import com.github.uragiristereo.mejiboard.domain.usecase.common.ConvertFileSizeUseCase
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.more.core.MoreState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val checkFileUseCase: CheckFileUseCase,
    private val getTagsInfoUseCase: GetTagsInfoUseCase,
    private val convertFileSizeUseCase: ConvertFileSizeUseCase,
) : ViewModel() {
    val state = mutableStateOf(MoreState())
    private val _state by state

    fun checkImage(original: Boolean = false) {
        if (original) {
            state.update {
                it.copy(
                    originalImageUrl = parseImageUrl(original = true),
                    originalImageSize = "Loading...",
                )
            }
        } else {
            state.update {
                it.copy(
                    imageUrl = parseImageUrl(original = false),
                    imageSize = "Loading...",
                )
            }
        }

        viewModelScope.launch {
            checkFileUseCase(
                url = if (original) _state.originalImageUrl else _state.imageUrl,
                onLoading = { },
                onSuccess = { headers ->
                    val size = headers["content-length"] ?: "0"

                    if (original) {
                        state.update { it.copy(originalImageSize = convertFileSizeUseCase(sizeBytes = size.toLong())) }
                    } else {
                        state.update { it.copy(imageSize = convertFileSizeUseCase(sizeBytes = size.toLong())) }
                    }
                },
                onFailed = {},
                onError = {},
            )
        }
    }

    fun getTagsInfo(names: String) {
        viewModelScope.launch {
            getTagsInfoUseCase(
                names = names,
                onLoading = { loading ->
                    state.update { it.copy(infoProgressVisible = loading) }
                },
                onSuccess = { data ->
                    state.update { it.copy(infoData = data) }
                },
                onFailed = { },
                onError = { },
            )
        }
    }

    private fun parseImageUrl(original: Boolean): String {
        val post = _state.selectedPost!!
        val imageType = File(post.image).extension
        val originalImageUrl = "https://img3.gelbooru.com/images/${post.directory}/${post.hash}.$imageType"

        return if (original) {
            originalImageUrl
        } else {
            when {
                post.sample == 1 && imageType != "gif" -> "https://img3.gelbooru.com/samples/${post.directory}/sample_${post.hash}.jpg"
                else -> originalImageUrl
            }
        }
    }

    fun convertFileSize(sizeBytes: Long): String {
        return convertFileSizeUseCase(sizeBytes)
    }
}