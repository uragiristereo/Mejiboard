package com.github.uragiristereo.mejiboard.data.repository.remote.provider

import com.github.uragiristereo.mejiboard.data.model.remote.adapter.MoshiDateAdapter
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.search.GelbooruSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toPostList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.toTagList
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.search.SafebooruOrgSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.toTagList
import com.github.uragiristereo.mejiboard.data.remote.api.GelbooruApi
import com.github.uragiristereo.mejiboard.data.remote.api.SafebooruApi
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
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
class GelbooruProviderRepository(
    okHttpClient: OkHttpClient,
    private val safe: Boolean,
) : ApiProviderRepository {
    override val provider = ApiProviders.Gelbooru

    private val moshi = Moshi.Builder()
        .add(MoshiDateAdapter(pattern = "EEE MMM dd HH:mm:ss ZZZ yyyy"))
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))

    private val client = retrofitBuilder
        .baseUrl(provider.baseUrl)
        .build()
        .create(GelbooruApi::class.java)

    private val clientSafe = retrofitBuilder
        .baseUrl(ApiProviders.SafebooruOrg.baseUrl)
        .build()
        .create(SafebooruApi::class.java)

    override suspend fun getPosts(tags: String, page: Int): PostsResult {
        val response = client.getPosts(
            tags = if (safe) "$tags rating:general" else tags,
            pageId = page,
            postsPerPage = provider.postsPerPage,
        )

        if (response.isSuccessful) {
            val posts = response.body()!!.toPostList()

            return PostsResult(
                data = posts,
                canLoadMore = posts.size == provider.postsPerPage,
            )
        }

        return PostsResult(
            errorMessage = response.errorBody()!!.string(),
            statusCode = response.code(),
        )
    }

    override suspend fun searchTerm(term: String): TagsResult {
        if (!safe) {
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
            errorMessage = response.errorBody()!!.string(),
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
            errorMessage = response.errorBody()!!.string(),
            statusCode = response.code(),
        )
    }
}