package com.github.uragiristereo.mejiboard.presentation.image.more

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.data.download.DownloadInstance
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.usecase.api.CheckFileUseCase
import com.github.uragiristereo.mejiboard.domain.usecase.api.GetTagsInfoUseCase
import com.github.uragiristereo.mejiboard.domain.usecase.common.ConvertFileSizeUseCase
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.more.core.MoreState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
                    originalImageUrl = it.selectedPost!!.originalImage.url,
                    originalImageSize = "Loading...",
                )
            }
        } else {
            state.update {
                it.copy(
                    imageUrl = it.selectedPost!!.scaledImage.url,
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
                onFailed = { },
                onError = { },
            )
        }
    }

    fun getTagsInfo(tags: String) {
        viewModelScope.launch {
            getTagsInfoUseCase(
                provider = ApiProviders.GelbooruSafe,
                tags = tags,
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

    fun convertFileSize(sizeBytes: Long): String {
        return convertFileSizeUseCase(sizeBytes)
    }

    fun trackShare(
        instance: DownloadInstance,
    ) {
        viewModelScope.launch {
            state.update { it.copy(dialogShown = true) }

            var lastDownloaded: Long

            while (instance.info.status == "downloading") {
                lastDownloaded = instance.info.downloaded

                delay(1000)

                state.update {
                    it.copy(
                        shareDownloadInfo = instance.info,
                        shareDownloadSpeed = instance.info.downloaded - lastDownloaded,
                    )
                }
            }

            state.update { it.copy(dialogShown = false) }
        }
    }
}