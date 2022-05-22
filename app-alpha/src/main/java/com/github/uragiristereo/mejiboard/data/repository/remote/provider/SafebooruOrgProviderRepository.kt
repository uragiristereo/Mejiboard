package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toTagList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.toTagList
import com.github.uragiristereo.mejiboard.data.remote.api.SafebooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.remote.ApiProviderRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SafebooruOrgProviderRepository(okHttpClient: OkHttpClient) : ApiProviderRepository {
    override val provider = ApiProviders.SafebooruOrg

    private val client = Retrofit.Builder()
        .baseUrl(provider.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(SafebooruApi::class.java)

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

        // we still need to use a part of Gelbooru's API since Safebooru.org doesn't provide it
        // it's fairly inacurrate but did the job
        val url = "https://gelbooru.com/index.php?page=dapi&s=tag&q=index&json=1&names=$tagsStr"

        val response = client.getTags(url)

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