package com.github.uragiristereo.mejiboard.domain.usecase.api

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.domain.repository.remote.ProvidersRepository
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val providersRepository: ProvidersRepository,
) {
    suspend operator fun invoke(
        provider: ApiProviders,
        tags: String,
        pageId: Int,
        onLoading: (loading: Boolean) -> Unit,
        onSuccess: (data: List<Post>, canLoadMore: Boolean) -> Unit,
        onFailed: (msg: String) -> Unit,
        onError: (t: Throwable) -> Unit,
    ) {
        onLoading(true)

        try {
            val result = providersRepository.getPosts(
                provider = provider,
                tags = tags,
                page = pageId,
            )

            if (result.errorMessage.isEmpty()) {
                onSuccess(result.data, result.canLoadMore)
            } else {
                onFailed("${result.statusCode}: \"${result.errorMessage}\"")
            }

            onLoading(false)
        } catch (t: Throwable) {
            when (t) {
                is CancellationException -> {}
                else -> onError(t)
            }

            onLoading(false)
        }
    }
}