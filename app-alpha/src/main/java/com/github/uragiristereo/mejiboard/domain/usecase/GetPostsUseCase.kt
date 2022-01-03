package com.github.uragiristereo.mejiboard.domain.usecase

import com.github.uragiristereo.mejiboard.common.extension.toPost
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Suppress("RemoveExplicitTypeArguments")
class GetPostsUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    operator fun invoke(
        pageId: Int,
        searchTags: String,
    ): Flow<Resource<List<Post>>> {
        return flow {
            emit(Resource.Loading())

            val response = networkRepository.api.getPosts(
                pid = pageId,
                tags = searchTags,
            )

            if (response.isSuccessful)
                response.body()?.let { result ->
                    val posts = result.post.map { it.toPost() }
                    emit(Resource.Success(posts))
                }
            else
                emit(Resource.Error<List<Post>>(response.errorBody().toString()))
        }
    }
}