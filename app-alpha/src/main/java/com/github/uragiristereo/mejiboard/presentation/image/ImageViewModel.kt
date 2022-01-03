package com.github.uragiristereo.mejiboard.presentation.image

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.common.helper.FileHelper.convertSize
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Tag
import com.github.uragiristereo.mejiboard.domain.usecase.CheckFileUseCase
import com.github.uragiristereo.mejiboard.domain.usecase.GetTagsInfoUseCase
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val checkFileUseCase: CheckFileUseCase,
    private val getTagsInfoUseCase: GetTagsInfoUseCase,
    val exoPlayerCache: SimpleCache,
) : ViewModel() {
    var imageSize by mutableStateOf("")
    var originalImageSize by mutableStateOf("")
    var shareModalVisible by mutableStateOf(false)

    var showOriginalImage by mutableStateOf(false)
    var originalImageShown by mutableStateOf(false)
    var originalImageUpdated by mutableStateOf(false)

    var infoData = mutableStateOf<List<Tag>>(listOf())
    var infoProgressVisible by mutableStateOf(false)
    var showTagsIsCollapsed by mutableStateOf(true)

    fun checkImage(url: String, original: Boolean = false) {
        if (original) originalImageSize = "Loading..." else imageSize = "Loading..."

        checkFileUseCase(url)
            .onEach { result ->
                if (result is Resource.Success) {
                    result.data?.let {
                        val size = it["content-length"] ?: "0"

                        if (original)
                            originalImageSize = convertSize(size.toInt())
                        else
                            imageSize = convertSize(size.toInt())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun getTagsInfo(names: String) {
        infoProgressVisible = true
        infoData.value = listOf()

        getTagsInfoUseCase(names)
            .onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        infoProgressVisible = false
                        infoData.value = result.data ?: emptyList()
                    }
                    is Resource.Loading -> {
                        infoData.value = listOf()
                        infoProgressVisible = true
                    }
                    is Resource.Error -> {}
                }
            }
            .launchIn(viewModelScope)
    }
}