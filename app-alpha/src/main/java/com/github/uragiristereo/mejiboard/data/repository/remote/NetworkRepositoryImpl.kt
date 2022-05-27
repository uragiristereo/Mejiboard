package com.github.uragiristereo.mejiboard.data.repository.remote

import com.github.uragiristereo.mejiboard.data.model.local.preferences.DohProvider
import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.remote.api.MejiboardApi
import com.github.uragiristereo.mejiboard.domain.repository.remote.NetworkRepository
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkRepositoryImpl(
    private val bootstrapOkHttpClient: OkHttpClient,
) : NetworkRepository {
    override var okHttpClient = bootstrapOkHttpClient

    override val api: MejiboardApi = Retrofit.Builder()
        .baseUrl("https://github.com")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(MejiboardApi::class.java)

    override fun renewInstance(
        useDnsOverHttps: Boolean,
        dohProvider: String,
    ) {
        okHttpClient = bootstrapOkHttpClient.newBuilder()
            .let {
                when {
                    useDnsOverHttps -> {
                        val dns = DnsOverHttps.Builder()
                            .client(bootstrapOkHttpClient)
                            .url(DohProvider.getUrl(dohProvider).toHttpUrl())
                            .build()

                        it.dns(dns)
                    }
                    else -> it
                }
            }
            .build()
    }
}