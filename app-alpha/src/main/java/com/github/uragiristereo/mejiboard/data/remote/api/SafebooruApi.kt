package com.github.uragiristereo.mejiboard.data.remote.api

import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.tags.GelbooruTagsResult
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.posts.SafebooruOrgPost
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.search.SafebooruOrgSearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SafebooruApi {
    @GET("/index.php?page=dapi&s=post&q=index&json=1")
    suspend fun getPosts(
        @Query("tags") tags: String,
        @Query("pid") pageId: Int,
        @Query("limit") postsPerPage: Int,
    ) : Response<List<SafebooruOrgPost>>

    @GET("/autocomplete.php")
    suspend fun searchTerm(@Query("q") term: String) : Response<List<SafebooruOrgSearch>>

    // we still need to use a part of Gelbooru's API since Safebooru.org doesn't provide it
    @GET
    suspend fun getTags(@Url url: String) : Response<GelbooruTagsResult>
}