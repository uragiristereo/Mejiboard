package com.github.uragiristereo.mejiboard.data.repository.remote

import com.github.uragiristereo.mejiboard.data.remote.api.MejiboardApi
import com.github.uragiristereo.mejiboard.domain.repository.remote.NetworkRepository
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
                            .url(
                                when (dohProvider) {
                                    "cloudflare" -> "https://cloudflare-dns.com/dns-query"
                                    "google" -> "https://dns.google/dns-query"
                                    "tuna" -> "https://101.6.6.6:8443/dns-query"
                                    else -> "https://cloudflare-dns.com/dns-query"
                                }.toHttpUrl()
                            )
                            .build()

                        it.dns(dns)
                    }
                    else -> it
                }
            }
            .build()
    }
}