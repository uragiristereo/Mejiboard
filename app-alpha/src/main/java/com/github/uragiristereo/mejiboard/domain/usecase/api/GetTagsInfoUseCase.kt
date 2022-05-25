package com.github.uragiristereo.mejiboard.domain.usecase.api

import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.repository.remote.ProvidersRepository
import javax.inject.Inject

class GetTagsInfoUseCase @Inject constructor(
    private val providersRepository: ProvidersRepository,
) {
    suspend operator fun invoke(
        provider: ApiProvider,
        tags: String,
        onLoading: (loading: Boolean) -> Unit,
        onSuccess: (data: List<Tag>) -> Unit,
        onFailed: (msg: String) -> Unit,
        onError: (t: Throwable) -> Unit,
    ) {
        onLoading(true)

        try {
            val tagList = tags.split(' ')

            val result = providersRepository.getTags(
                provider = provider,
                tags = tagList,
            )

            if (result.errorMessage.isEmpty()) {
                onSuccess(result.data)
            } else {
                onFailed(result.errorMessage)
            }

            onLoading(false)
        } catch (t: Throwable) {
            when (t) {
                is java.util.concurrent.CancellationException -> {}
                else -> onError(t)
            }

            onLoading(false)
        }
    }
}