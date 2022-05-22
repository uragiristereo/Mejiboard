package com.github.uragiristereo.mejiboard.data.repository.remote

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.repository.remote.provider.GelbooruProviderRepository
import com.github.uragiristereo.mejiboard.data.repository.remote.provider.GelbooruSafeProviderRepository
import com.github.uragiristereo.mejiboard.data.repository.remote.provider.SafebooruOrgProviderRepository
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import com.github.uragiristereo.mejiboard.domain.repository.remote.ApiProviderRepository
import com.github.uragiristereo.mejiboard.domain.repository.remote.ProvidersRepository
import javax.inject.Inject

class ProvidersRepositoryImpl @Inject constructor(
    networkRepository: NetworkRepository,
) : ProvidersRepository {
    private val okHttpClient = networkRepository.okHttpClient

    override val providers: Map<ApiProviders, ApiProviderRepository> = mapOf(
        ApiProviders.Gelbooru to GelbooruProviderRepository(okHttpClient),
        ApiProviders.GelbooruSafe to GelbooruSafeProviderRepository(okHttpClient),
        ApiProviders.SafebooruOrg to SafebooruOrgProviderRepository(okHttpClient),
    )

    override suspend fun getPosts(provider: ApiProviders, tags: String, page: Int): PostsResult {
        val providerRepository = providers[provider]!!

        return providerRepository.getPosts(tags, page)
    }

    override suspend fun searchTerm(provider: ApiProviders, term: String): TagsResult {
        val providerRepository = providers[provider]!!

        return providerRepository.searchTerm(term)
    }

    override suspend fun getTags(provider: ApiProviders, tags: List<String>): TagsResult {
        val providerRepository = providers[provider]!!

        return providerRepository.getTags(tags)
    }
}