package com.uragiristereo.mejiboard.model.network

import android.content.Context
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.util.CoilUtils
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps

class NetworkInstance(
    context: Context,
    bootstrapOkHttpClient: OkHttpClient
) {
    private val _bootstrapOkHttpClient = bootstrapOkHttpClient
    private val _context = context

    var okHttpClient = bootstrapOkHttpClient

    var api = DataRepository.create(okHttpClient)

    var imageLoader = ImageLoader.Builder(_context)
        .okHttpClient(
            okHttpClient.newBuilder()
                .cache(CoilUtils.createDefaultCache(_context))
                .build()
        )
        .build()

    fun renewInstance(
        useDnsOverHttps: Boolean,
        dohProvider: String
    ) {
        okHttpClient = createOkHttpClient(_bootstrapOkHttpClient, useDnsOverHttps, dohProvider)
        api = DataRepository.create(okHttpClient)

        imageLoader = ImageLoader.Builder(_context)
            .okHttpClient(
                okHttpClient.newBuilder()
                    .cache(CoilUtils.createDefaultCache(_context))
                    .build()
            )
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