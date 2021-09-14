package com.uragiristereo.mejiboard.model.network

import com.squareup.moshi.Moshi
import com.uragiristereo.mejiboard.util.BASE_URL
import com.uragiristereo.mejiboard.util.CustomDateAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object DataRepository {
    fun create(
        okHttpClient: OkHttpClient
    ): DataServices {
        val moshiBuilder = Moshi.Builder()
            .add(CustomDateAdapter())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshiBuilder))
            .baseUrl(BASE_URL)
            .build()

        return retrofit.create(DataServices::class.java)
    }
}