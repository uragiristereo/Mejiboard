package com.github.uragiristereo.mejiboard.data.remote.api

import com.github.uragiristereo.mejiboard.data.model.remote.app.AppUpdate
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Streaming
import retrofit2.http.Url

interface MejiboardApi {
    @Streaming
    @GET
    fun downloadFile(@Url url: String): Call<ResponseBody>

    @HEAD
    suspend fun checkFile(@Url url: String): Response<Void>

    @GET
    fun checkForUpdate(@Url url: String = "https://raw.githubusercontent.com/uragiristereo/Mejiboard/main/app-alpha/update.json"): Call<AppUpdate>
}