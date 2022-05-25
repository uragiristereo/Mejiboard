package com.github.uragiristereo.mejiboard.presentation.image.more.core

import com.github.uragiristereo.mejiboard.data.model.local.DownloadInfo
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag

data class MoreState(
    val selectedPost: Post? = null,
    val imageUrl: String = "",
    val imageSize: String = "",
    val originalImageUrl: String = "",
    val originalImageSize: String = "",
    val isShowTagsCollapsed: Boolean = true,
    val dialogShown: Boolean = false,
    val shareDownloadInfo: DownloadInfo = DownloadInfo(),
    val shareDownloadSpeed: Long = 0,
    val infoProgressVisible: Boolean = false,
    val infoData: List<Tag> = emptyList(),
    val selectedProvider: ApiProvider = ApiProviders.Gelbooru,
)
