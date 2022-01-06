package com.github.uragiristereo.mejiboard.data.remote.api

import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.data.dto.api.post.PostResultDto
import com.github.uragiristereo.mejiboard.data.dto.api.tag.TagResultDto
import com.github.uragiristereo.mejiboard.data.model.AppUpdate
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface BoardApi {
    @GET("/index.php?page=dapi&s=post&q=index&limit=100&json=1")
    suspend fun getPosts(
        @Query("pid") pid: Int,
        @Query("tags") tags: String,
    ): Response<PostResultDto>

    @GET("/index.php?page=autocomplete2&type=tag_query&limit=10")
    suspend fun getTags(@Query("term") term: String): Response<List<SearchDto>>

    @GET("/index.php?page=dapi&s=tag&q=index&json=1")
    suspend fun getTagsInfo(@Query("names") names: String): Response<TagResultDto>

    @Streaming
    @GET
    fun downloadFile(@Url url: String): Call<ResponseBody>

    @HEAD
    suspend fun checkFile(@Url url: String): Response<Void>

    @GET
    fun checkForUpdate(@Url url: String = "https://raw.githubusercontent.com/uragiristereo/Mejiboard/main/app-alpha/update.json"): Call<AppUpdate>
}