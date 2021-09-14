package com.uragiristereo.mejiboard.model.network

import com.uragiristereo.mejiboard.model.Post
import com.uragiristereo.mejiboard.model.Search
import com.uragiristereo.mejiboard.model.Tag
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Query
import retrofit2.http.Url

interface DataServices {
    @GET("/index.php?page=dapi&s=post&q=index&limit=100&json=1")
    fun getPosts(@Query("pid") pid: Int, @Query("tags") tags: String): Call<List<Post>>

    @GET("/index.php?page=autocomplete2&type=tag_query&limit=10&")
    fun getTags(@Query("term") term: String): Call<List<Search>>

    @GET
    fun downloadImage(@Url url: String): Call<ResponseBody>

    @HEAD
    fun checkImage(@Url url: String): Call<Void>

    @GET("/index.php?page=dapi&s=tag&q=index&json=1&")
    fun getTagsInfo(@Query("names") names: String): Call<List<Tag>>
}