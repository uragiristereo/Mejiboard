package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.common.RatingFilter
import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.search.GelbooruSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toTagList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.search.SafebooruOrgSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.toTagList
import com.github.uragiristereo.mejiboard.data.remote.provider.GelbooruApi
import com.github.uragiristereo.mejiboard.data.remote.provider.SafebooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.remote.ApiProviderRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("BlockingMethodInNonBlockingContext")
class GelbooruProviderRepository(okHttpClient: OkHttpClient) : ApiProviderRepository {
    override val provider = ApiProviders.Gelbooru

    private val moshi = Moshi.Builder()
        .add(MoshiDateAdapter(pattern = "EEE MMM dd HH:mm:ss ZZZ yyyy"))
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))

    private val client = retrofitBuilder
        .baseUrl(provider.baseUrl())
        .build()
        .create(GelbooruApi::class.java)

    private val clientSafe = retrofitBuilder
        .baseUrl(ApiProviders.SafebooruOrg.baseUrl())
        .build()
        .create(SafebooruApi::class.java)

    override suspend fun getPosts(tags: String, page: Int, filters: List<Rating>): PostsResult {
        // Gelbooru allows unlimited tags to search so we can filter ratings from server
        val filterTags = when (filters) {
            RatingFilter.GENERAL_ONLY -> " rating:general"
            RatingFilter.SAFE -> " -rating:questionable -rating:explicit"
            RatingFilter.NO_EXPLICIT -> " -rating:explicit"
            RatingFilter.UNFILTERED -> ""
            else -> " rating:general"
        }

        val response = client.getPosts(
            tags = tags + filterTags,
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
        if (filters == RatingFilter.NO_EXPLICIT || filters == RatingFilter.UNFILTERED) {
            val response = client.searchTerm(term)

            if (response.isSuccessful) {
                return TagsResult(
                    data = response.body()!!.toTagList(),
                )
            }

            return TagsResult(
                errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
            )
        } else {
            return searchTermSafe(term)
        }
    }

    private suspend fun searchTermSafe(term: String): TagsResult {
        lateinit var response: Response<List<GelbooruSearch>>
        lateinit var responseFromSafebooruOrg: Response<List<SafebooruOrgSearch>>

        withContext(Dispatchers.Default) {
            val tasks = listOf(
                async {
                    // we ONLY need to get tag's post count from Gelbooru
                    response = client.searchTerm(term)
                },
                async {
                    // we need to get tag list from SafebooruOrg
                    responseFromSafebooruOrg = clientSafe.searchTerm(term)
                },
            )

            tasks.awaitAll()
        }

        if (response.isSuccessful && responseFromSafebooruOrg.isSuccessful) {
            val gelbooruTagList = response.body()!!.toTagList()
            val mergedTagList = responseFromSafebooruOrg.body()!!
                .toTagList()
                .map { safebooruOrgTag ->
                    // merge both data
                    val gelbooruTag =
                        gelbooruTagList.firstOrNull { it.name == safebooruOrgTag.name }

                    gelbooruTag ?: safebooruOrgTag
                }

            return TagsResult(
                data = mergedTagList,
            )
        }

        return TagsResult(
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
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
            errorMessage = "${response.code()}: ${response.errorBody()!!.string()}",
        )
    }
}