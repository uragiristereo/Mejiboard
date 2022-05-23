package com.github.uragiristereo.mejiboard.domain.repository.remote

import com.github.uragiristereo.mejiboard.data.remote.api.MejiboardApi
import okhttp3.OkHttpClient

interface NetworkRepository {
    var okHttpClient: OkHttpClient

    val api: MejiboardApi

    fun renewInstance(useDnsOverHttps: Boolean, dohProvider: String)
}