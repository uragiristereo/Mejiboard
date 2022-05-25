package com.github.uragiristereo.mejiboard.presentation.main.core

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider

data class MainState(
    val selectedProvider: ApiProvider = ApiProviders.Gelbooru,
)
