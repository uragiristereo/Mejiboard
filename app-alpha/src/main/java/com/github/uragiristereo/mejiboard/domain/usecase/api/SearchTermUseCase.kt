package com.github.uragiristereo.mejiboard.domain.usecase.api

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.repository.remote.ProvidersRepository
import kotlinx.coroutines.delay
import java.util.concurrent.CancellationException
import javax.inject.Inject

class SearchTermUseCase @Inject constructor(
    private val providersRepository: ProvidersRepository,
) {
    suspend operator fun invoke(
        provider: ApiProvider,
        filters: List<Rating>,
        term: String,
        onLoading: (loading: Boolean) -> Unit,
        onSuccess: (data: List<Tag>) -> Unit,
        onFailed: (msg: String) -> Unit,
        onError: (t: Throwable) -> Unit,
    ) {
        onLoading(true)

        delay(timeMillis = Constants.API_DELAY)

        try {
            val result = providersRepository.searchTerm(
                provider = provider,
                filters = filters,
                term = term,
            )

            if (result.errorMessage.isEmpty()) {
                onSuccess(result.data)
            } else {
                onFailed(result.errorMessage)
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