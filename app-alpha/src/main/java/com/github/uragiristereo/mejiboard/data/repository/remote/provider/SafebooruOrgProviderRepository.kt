package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toTagList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.toTagList
import com.github.uragiristereo.mejiboard.data.remote.provider.GelbooruApi
import com.github.uragiristereo.mejiboard.data.remote.provider.SafebooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.remote.ApiProviderRepository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("BlockingMethodInNonBlockingContext")
class SafebooruOrgProviderRepository(okHttpClient: OkHttpClient) : ApiProviderRepository {
    override val provider = ApiProviders.SafebooruOrg

    private val moshi = Moshi.Builder()
        .add(MoshiDateAdapter(pattern = "EEE MMM dd HH:mm:ss ZZZ yyyy"))
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))

    private val client = retrofitBuilder
        .baseUrl(provider.baseUrl())
        .build()
        .create(SafebooruApi::class.java)

    private val gelbooruClient = retrofitBuilder
        .baseUrl(ApiProviders.Gelbooru.baseUrl())
        .build()
        .create(GelbooruApi::class.java)

    override suspend fun getPosts(tags: String, page: Int, filters: List<Rating>): PostsResult {
        val response = client.getPosts(
            tags = tags,
            pageId = page,
            postsPerPage = Constants.POSTS_PER_PAGE,
        )

        if (response.isSuccessful) {
            val posts = response.body()!!.toPostList()
            return PostsResult(
                data = posts,
                canLoadMore = posts.size == Constants.POSTS_PER_PAGE,
            )
        }

        return PostsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }

    override suspend fun searchTerm(term: String, filters: List<Rating>): TagsResult {
        val response = client.searchTerm(term)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }

    override suspend fun getTags(tags: List<String>): TagsResult {
        val tagsStr = tags.joinToString(separator = " ")

        // we still need to use a part of Gelbooru's API since Safebooru.org doesn't provide it
        // it's fairly inaccurate but did the job
        val response = gelbooruClient.getTags(tagsStr)

        if (response.isSuccessful) {
            return TagsResult(
                data = response.body()!!.toTagList(),
            )
        }

        return TagsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }
}