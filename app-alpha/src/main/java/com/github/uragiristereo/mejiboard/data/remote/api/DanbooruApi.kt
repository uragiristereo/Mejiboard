package com.github.uragiristereo.mejiboard.data.remote.api

import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.posts.DanbooruPost
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.search.DanbooruSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.tags.DanbooruTag
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DanbooruApi {
    @GET("/posts.json")
    suspend fun getPosts(
        @Query("tags") tags: String,
        @Query("page") pageId: Int,
        @Query("limit") postsPerPage: Int,
    ): Response<List<DanbooruPost>>

    @GET("/autocomplete.json?&search[type]=tag_query&limit=10")
    suspend fun searchTerm(@Query("search[query]") term: String): Response<List<DanbooruSearch>>

    @GET("/tags.json")
    suspend fun getTags(@Query("search[name][]") tags: List<String>): Response<List<DanbooruTag>>
}