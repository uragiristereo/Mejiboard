package com.github.uragiristereo.mejiboard.domain.repository.remote

import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.PostsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagsResult

interface ApiProviderRepository {
    val provider: ApiProvider

    suspend fun getPosts(tags: String, page: Int, filters: List<Rating>): PostsResult

    suspend fun searchTerm(term: String, filters: List<Rating>): TagsResult

    suspend fun getTags(tags: List<String>): TagsResult
}