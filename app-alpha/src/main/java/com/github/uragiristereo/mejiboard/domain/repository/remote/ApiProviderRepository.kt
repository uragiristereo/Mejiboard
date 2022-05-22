package com.github.uragiristereo.mejiboard.domain.repository.remote

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult

interface ApiProviderRepository {
    val provider: ApiProviders

    suspend fun getPosts(tags: String, page: Int): PostsResult

    suspend fun searchTerm(term: String): TagsResult

    suspend fun getTags(tags: List<String>): TagsResult
}