package com.github.uragiristereo.mejiboard.data.repository.remote

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.repository.remote.provider.DanbooruProviderRepository
import com.github.uragiristereo.mejiboard.data.repository.remote.provider.GelbooruProviderRepository
import com.github.uragiristereo.mejiboard.data.repository.remote.provider.SafebooruOrgProviderRepository
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.remote.NetworkRepository
import com.github.uragiristereo.mejiboard.domain.repository.remote.ProvidersRepository
import javax.inject.Inject

class ProvidersRepositoryImpl @Inject constructor(
    networkRepository: NetworkRepository,
) : ProvidersRepository {
    private val okHttpClient = networkRepository.okHttpClient

    override var providers = mapOf(
        ApiProviders.Gelbooru to GelbooruProviderRepository(okHttpClient),
        ApiProviders.Danbooru to DanbooruProviderRepository(okHttpClient),
        ApiProviders.SafebooruOrg to SafebooruOrgProviderRepository(okHttpClient),
    )

    override suspend fun getPosts(provider: ApiProvider, tags: String, page: Int, filters: List<Rating>): PostsResult {
        val providerRepository = providers[provider]!!

        return providerRepository.getPosts(tags, page, filters)
    }

    override suspend fun searchTerm(provider: ApiProvider, term: String, filters: List<Rating>): TagsResult {
        val providerRepository = providers[provider]!!

        return providerRepository.searchTerm(term, filters)
    }

    override suspend fun getTags(provider: ApiProvider, tags: List<String>): TagsResult {
        val providerRepository = providers[provider]!!

        return providerRepository.getTags(tags)
    }
}