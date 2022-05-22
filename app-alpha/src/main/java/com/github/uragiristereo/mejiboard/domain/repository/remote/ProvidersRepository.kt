package com.github.uragiristereo.mejiboard.domain.repository.remote

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult

interface ProvidersRepository {
    val providers: Map<ApiProviders, ApiProviderRepository>

    suspend fun getPosts(provider: ApiProviders, tags: String, page: Int): PostsResult

    suspend fun searchTerm(provider: ApiProviders, term: String): TagsResult

    suspend fun getTags(provider: ApiProviders, tags: List<String>): TagsResult
}