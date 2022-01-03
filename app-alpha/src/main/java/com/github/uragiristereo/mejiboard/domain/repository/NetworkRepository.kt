package com.github.uragiristereo.mejiboard.domain.repository

import android.content.Context
import coil.ImageLoader
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.common.CustomDateAdapter
import com.github.uragiristereo.mejiboard.data.remote.api.BoardApi
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class NetworkRepository(
    private val context: Context,
    private val bootstrapOkHttpClient: OkHttpClient,
) {
    private val moshiBuilder = Moshi.Builder()
        .add(CustomDateAdapter())
        .build()

    var okHttpClient = bootstrapOkHttpClient

    var api: BoardApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshiBuilder))
        .build()
        .create(BoardApi::class.java)

    var imageLoader = ImageLoader.Builder(context)
        .okHttpClient(okHttpClient)
        .build()

    fun renewInstance(
        useDnsOverHttps: Boolean,
        dohProvider: String
    ) {
        okHttpClient = createOkHttpClient(
            bootstrapOkHttpClient = bootstrapOkHttpClient,
            useDnsOverHttps = useDnsOverHttps,
            dohProvider = dohProvider,
        )
        api = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshiBuilder))
            .build()
            .create(BoardApi::class.java)

        imageLoader = ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()
    }

    private fun createOkHttpClient(
        bootstrapOkHttpClient: OkHttpClient,
        useDnsOverHttps: Boolean,
        dohProvider: String,
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

        val okHttpClientBuilder = bootstrapOkHttpClient.newBuilder()

        return if (useDnsOverHttps)
            okHttpClientBuilder
                .dns(dns)
                .build()
        else
            okHttpClientBuilder.build()
    }
}