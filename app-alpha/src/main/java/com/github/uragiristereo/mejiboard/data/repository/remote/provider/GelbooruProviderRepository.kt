package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toTagList
import com.github.uragiristereo.mejiboard.data.remote.api.GelbooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.remote.ApiProviderRepository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GelbooruProviderRepository(okHttpClient: OkHttpClient) : ApiProviderRepository {
    override val provider = ApiProviders.Gelbooru

    private val moshi = Moshi.Builder()
        .add(MoshiDateAdapter(pattern = "EEE MMM dd HH:mm:ss ZZZ yyyy"))
        .build()

    private val client = Retrofit.Builder()
        .baseUrl(provider.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GelbooruApi::class.java)

    override suspend fun getPosts(tags: String, page: Int): PostsResult {
        val response = client.getPosts(
            tags = tags,
            pageId = page,
            postsPerPage = provider.postsPerPage,
        )

        if (response.isSuccessful) {
            return PostsResult(
                data = response.body()!!.toPostList(),
            )
        }

        return PostsResult(
            errorMessage = response.errorBody().toString(),
            statusCode = response.code(),
        )
    }

    override suspend fun searchTerm(term: String): TagsResult {
        val response = client.searchTerm(term)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = response.errorBody().toString(),
            statusCode = response.code(),
        )
    }

    override suspend fun getTags(tags: List<String>): TagsResult {
        val tagsStr = tags.joinToString(separator = " ")

        val response = client.getTags(tagsStr)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = response.errorBody().toString(),
            statusCode = response.code(),
        )
    }
}