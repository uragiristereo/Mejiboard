package com.github.uragiristereo.mejiboard.presentation.main.core

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders

data class MainState(
    val selectedProvider: ApiProviders = ApiProviders.GelbooruSafe,
)
