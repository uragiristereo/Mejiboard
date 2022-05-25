package com.github.uragiristereo.mejiboard.domain.repository.remote

import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult

interface ProvidersRepository {
    val providers: Map<ApiProvider, ApiProviderRepository>

    suspend fun getPosts(provider: ApiProvider, tags: String, page: Int, filters: List<Rating>): PostsResult

    suspend fun searchTerm(provider: ApiProvider, term: String, filters: List<Rating>): TagsResult

    suspend fun getTags(provider: ApiProvider, tags: List<String>): TagsResult
}