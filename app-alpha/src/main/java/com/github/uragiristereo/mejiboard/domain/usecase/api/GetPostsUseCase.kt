package com.github.uragiristereo.mejiboard.domain.usecase.api

import com.github.uragiristereo.mejiboard.common.extension.toPost
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(
        tags: String,
        pageId: Int,
        onLoading: (loading: Boolean) -> Unit,
        onSuccess: (data: List<Post>) -> Unit,
        onFailed: (msg: String) -> Unit,
        onError: (t: Throwable) -> Unit,
    ) {
        onLoading(true)

        try {
            val response = networkRepository.api.getPosts(
                pid = pageId,
                tags = tags,
            )

            if (response.isSuccessful) {
                response.body()?.let { data ->
                    if (data.post != null) {
                        onSuccess(data.post.map { it.toPost() })
                    } else {
                        onSuccess(emptyList())
                    }
                }
            } else {
                onFailed(response.raw().body.toString())
            }

            onLoading(false)
        } catch (t: Throwable) {
            when (t) {
                is CancellationException -> {}
                else -> {
                    onLoading(false)
                    onError(t)
                }
            }
        }
    }
}