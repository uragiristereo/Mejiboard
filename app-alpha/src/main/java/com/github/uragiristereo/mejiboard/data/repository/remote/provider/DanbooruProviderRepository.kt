package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.toTagList
import com.github.uragiristereo.mejiboard.data.remote.api.DanbooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.remote.ApiProviderRepository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("BlockingMethodInNonBlockingContext")
class DanbooruProviderRepository(
    okHttpClient: OkHttpClient
) : ApiProviderRepository {
    override val provider = ApiProviders.Danbooru

    private val moshi = Moshi.Builder()
        .add(MoshiDateAdapter(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
        .build()

    private val client = Retrofit.Builder()
        .baseUrl(provider.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(DanbooruApi::class.java)

    override suspend fun getPosts(tags: String, page: Int): PostsResult {
        val response = client.getPosts(
            tags = tags,
            pageId = page + 1,
            postsPerPage = provider.postsPerPage,
        )

        if (response.isSuccessful) {
            val posts = response.body()!!.toPostList()

            return PostsResult(
                data = posts.filter { it.id != 0 },
                canLoadMore = posts.size == provider.postsPerPage,
            )
        }

        return PostsResult(
            errorMessage = response.errorBody()!!.string(),
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
            errorMessage = response.errorBody()!!.string(),
            statusCode = response.code(),
        )
    }

    override suspend fun getTags(tags: List<String>): TagsResult {
        val response = client.getTags(tags)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = response.errorBody()!!.string(),
            statusCode = response.code(),
        )
    }
}