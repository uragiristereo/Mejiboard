package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.toTagList
import com.github.uragiristereo.mejiboard.data.remote.provider.DanbooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
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
        .baseUrl(provider.baseUrl())
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(DanbooruApi::class.java)

    override suspend fun getPosts(tags: String, page: Int, filters: List<Rating>): PostsResult {
        val response = client.getPosts(
            tags = tags,
            pageId = page + 1,
            postsPerPage = Constants.POSTS_PER_PAGE,
        )

        if (response.isSuccessful) {
            val posts = response.body()!!.toPostList()

            return PostsResult(
                data = posts.filter { it.id != 0 },
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
        val response = client.getTags(tags)

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