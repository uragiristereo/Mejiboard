package com.github.uragiristereo.mejiboard.model.network

import android.content.Context
import coil.ImageLoader
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps

class NetworkInstance(
    private val context: Context,
    private val bootstrapOkHttpClient: OkHttpClient
) {
    var okHttpClient = bootstrapOkHttpClient

    var api = DataRepository.create(okHttpClient)

    var imageLoader = ImageLoader.Builder(context)
        .okHttpClient(okHttpClient)
        .build()

    fun renewInstance(
        useDnsOverHttps: Boolean,
        dohProvider: String
    ) {
        okHttpClient = createOkHttpClient(bootstrapOkHttpClient, useDnsOverHttps, dohProvider)
        api = DataRepository.create(okHttpClient)

        imageLoader = ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()
    }

    private fun createOkHttpClient(
        bootstrapOkHttpClient: OkHttpClient,
        useDnsOverHttps: Boolean,
        dohProvider: String
    ): OkHttpClient {
        val dohProviderUrl =
            when (dohProvider) {
                "cloudflare" -> "https://cloudflare-dns.com/dns-query"
                "google" -> "https://dns.google/dns-query"
                "tuna" -> "https://101.6.6.6:8443/dns-query"
                else -> "https://cloudflare-dns.com/dns-query"
            }

        val dns = DnsOverHttps.Builder()
            .client(bootstrapOkHttpClient)
            .url(dohProviderUrl.toHttpUrl())
            .build()

        val okHttpClientBuilder = bootstrapOkHttpClient
            .newBuilder()

        return if (useDnsOverHttps)
            okHttpClientBuilder
                .dns(dns)
                .build()
        else
            okHttpClientBuilder.build()
    }
}